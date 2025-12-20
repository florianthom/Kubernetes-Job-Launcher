package com.florianthom.kubernetesjoblauncher;

import com.florianthom.kubernetesjoblauncher.config.LauncherProperties;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class JobLauncherService {
    private final BatchV1Api batchApi;
    private final CoreV1Api coreApi;
    private final LauncherProperties launcherProperties;
    @Value("${kubernetesjoblauncher.queue.inputqueueurl}")
    private String inputQueueUrl;
    private final SqsClient sqsClient;

    public JobLauncherService(BatchV1Api batchApi, CoreV1Api coreApi, LauncherProperties launcherProperties, SqsClient sqsClient) {
        this.batchApi = batchApi;
        this.coreApi = coreApi;
        this.launcherProperties = launcherProperties;
        this.sqsClient = sqsClient;
    }

    public V1Job launchJob(){
        try {
            V1ConfigMap cm = coreApi.readNamespacedConfigMap(
                    launcherProperties.getJobtemplateconfigmapname(), launcherProperties.getNamespace()
            ).execute();
            V1Job job = Yaml.loadAs(cm.getData().get(launcherProperties.getJobtemplateconfigmapkey()), V1Job.class);
            job.getMetadata().setName(launcherProperties.getJobname() + "-" + UUID.randomUUID());
            job.getMetadata().setLabels(Map.of("app", launcherProperties.getJobname()));
            job.getSpec().getTemplate().getSpec().getContainers().getFirst().setEnv(List.of(
                new V1EnvVar().name("TEST_ENV_VAR").value("some-value")
            ));
            return batchApi.createNamespacedJob(launcherProperties.getNamespace(), job).execute();
        } catch (ApiException e) { throw new RuntimeException(e); }
    }

    @Scheduled(fixedDelayString = "${kubernetesjoblauncher.launcher.checksqsintervallms}")
    public void scheduledLauncher() throws ApiException {
        int visibleMessages = getVisibleMessages();
        System.out.println("Got following number of visible messages in queue: " + visibleMessages);
        IntStream.range(0, visibleMessages).forEach(i -> launchJob());
    }

    private int getVisibleMessages() {
        var attrRequest = GetQueueAttributesRequest.builder()
                .queueUrl(inputQueueUrl)
                .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
                .build();

        Map<QueueAttributeName, String> attrs = sqsClient.getQueueAttributes(attrRequest).attributes();
        return Integer.parseInt(attrs.getOrDefault(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES, "0"));
    }
}