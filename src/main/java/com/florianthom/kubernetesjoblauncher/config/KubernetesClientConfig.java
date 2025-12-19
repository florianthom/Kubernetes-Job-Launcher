package com.florianthom.kubernetesjoblauncher.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class KubernetesClientConfig {

    @Bean
    public ApiClient apiClient() throws IOException {
        ApiClient client = Config.defaultClient();
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
        return client;
    }

    @Bean
    public BatchV1Api batchV1Api(ApiClient client) {
        return new BatchV1Api(client);
    }
}