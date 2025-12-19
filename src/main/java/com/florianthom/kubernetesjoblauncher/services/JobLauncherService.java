package com.florianthom.kubernetesjoblauncher.services;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public JobLauncherService(BatchV1Api batchApi) {
        this.batchApi = batchApi;
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
}