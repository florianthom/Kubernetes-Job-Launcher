package com.florianthom.kubernetesjoblauncher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "kubernetesjoblauncher")
public record AppProperties(Launcher launcher, Queue queue) {
    public record Launcher(
            String namespace,
            long checksqsintervallms,
            String jobtemplateconfigmapname,
            String jobtemplateconfigmapkey,
            String jobname
    ) {}

    public record Queue(
            String endpoint,
            String accesskey,
            String secretkey,
            String region,
            String inputqueueurl
    ) {}
}