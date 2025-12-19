package com.florianthom.kubernetesjoblauncher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class AwsSqsConfig {

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
}