package com.florianthom.kubernetesjoblauncher;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.io.IOException;
import java.net.URI;

@Configuration
public class AppConfig {

    @Value("${kubernetesjoblauncher.queue.endpoint}")
    private String queueEndpoint;

    @Value("${kubernetesjoblauncher.queue.accesskey}")
    private String accessKey;

    @Value("${kubernetesjoblauncher.queue.secretkey}")
    private String secretKey;

    @Value("${kubernetesjoblauncher.queue.region}")
    private String region;

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(queueEndpoint))
                // .credentialsProvider(ProfileCredentialsProvider.create())
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

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

    @Bean
    public CoreV1Api coreV1Api(ApiClient client) {
        return new CoreV1Api(client);
    }
}