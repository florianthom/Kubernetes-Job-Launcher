package com.florianthom.kubernetesjoblauncher;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.util.Yaml;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@EnableScheduling
public class KubernetesjoblauncherApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubernetesjoblauncherApplication.class, args);
	}

    @Service
    static class JobLauncherService {
        private final BatchV1Api batchApi;
        private final CoreV1Api coreApi;
        private final AppProperties props;
        private final SqsClient sqsClient;

        public JobLauncherService(BatchV1Api batchApi, CoreV1Api coreApi, AppProperties props, SqsClient sqsClient) {
            this.batchApi = batchApi;
            this.coreApi = coreApi;
            this.props = props;
            this.sqsClient = sqsClient;
        }

        @Scheduled(fixedDelayString = "${kubernetesjoblauncher.launcher.checksqsintervallms}")
        public void run() throws ApiException {
            IntStream.range(0, getVisibleMessages()).forEach(i -> launchJob());
        }

        public V1Job launchJob(){
            try {
                var job = loadJobFromConfigMap();
                job.getMetadata().setName(props.launcher().jobname() + "-" + UUID.randomUUID());
                job.getMetadata().setLabels(Map.of("app", props.launcher().jobname()));
                job.getSpec().getTemplate().getSpec().getContainers().getFirst()
                        .setEnv(List.of(new V1EnvVar().name("TEST_ENV_VAR").value("some-value")));
                return batchApi.createNamespacedJob(props.launcher().namespace(), job).execute();
            } catch (ApiException e) { throw new RuntimeException(e); }
        }

        private V1Job loadJobFromConfigMap() throws ApiException {
            var cm = coreApi.readNamespacedConfigMap(
                    props.launcher().jobtemplateconfigmapname(),
                    props.launcher().namespace()
            ).execute();
            return Yaml.loadAs(cm.getData().get(props.launcher().jobtemplateconfigmapkey()), V1Job.class);
        }

        private int getVisibleMessages() {
            var attrRequest = GetQueueAttributesRequest.builder()
                    .queueUrl(props.queue().inputqueueurl())
                    .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
                    .build();

            String numberMessages = sqsClient.getQueueAttributes(attrRequest)
                    .attributes()
                    .getOrDefault(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES, "0");

            System.out.println("Got following number of visible messages in queue: " + numberMessages);
            return Integer.parseInt(numberMessages);
        }
    }
}
