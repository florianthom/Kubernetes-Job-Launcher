package com.florianthom.kubernetesjoblauncher.services;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JobLauncherService {

    private final BatchV1Api batchApi;
    @Value("${kubernetesjoblauncher.job.name}")
    private String jobName;
    @Value("${kubernetesjoblauncher.job.namespace}")
    private String jobNamespace;
    @Value("${kubernetesjoblauncher.launcher.checksqsintervallms}")
    private String checksqsintervallms;
    @Value("${kubernetesjoblauncher.queue.inputqueueurl}")
    private String inputQueueUrl;
    private final SqsClient sqsClient;


    public JobLauncherService(BatchV1Api batchApi, SqsClient sqsClient) {
        this.batchApi = batchApi;
        this.sqsClient = sqsClient;
    }

    public V1Job launchJob() throws ApiException {
        var job = new V1Job()
                .metadata(new V1ObjectMeta()
                        .name(this.jobName + UUID.randomUUID())
                        .labels(Map.of("app", this.jobName)))
                .spec(new V1JobSpec()
                        .backoffLimit(0) // retries handled by launcher, not Kubernetes
                        .template(new V1PodTemplateSpec()
                                .spec(new V1PodSpec()
                                        .restartPolicy("Never")
                                        .containers(List.of(
                                                new V1Container()
                                                        .name("example")
                                                        .image("busybox")
                                                        .command(List.of("sh", "-c", "echo Hello && exit 0"))
                                        )))));

        return batchApi.createNamespacedJob(jobNamespace, job).execute();
    }

    @Scheduled(fixedDelayString = "${kubernetesjoblauncher.launcher.checksqsintervallms}")
    public void scheduledLauncher(){
        int visibleMessages = getVisibleMessages();
        System.out.println("Got following number of visible messages in queue: " + visibleMessages);

        if (visibleMessages < 1) {
            return;
        }

        for (int i = 0; i < visibleMessages; i++) {
            try {
                launchJob();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int getVisibleMessages() {
        Map<QueueAttributeName, String> attrs = sqsClient.getQueueAttributes(
                GetQueueAttributesRequest.builder()
                        .queueUrl(inputQueueUrl)
                        .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
                        .build()
                ).attributes();

        return Integer.parseInt(attrs.getOrDefault(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES, "0")
        );
    }
}