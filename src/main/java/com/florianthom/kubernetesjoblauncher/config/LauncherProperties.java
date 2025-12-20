package com.florianthom.kubernetesjoblauncher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kubernetesjoblauncher.launcher")
public class LauncherProperties {

    private String namespace;
    private long checksqsintervallms;
    private String jobtemplateconfigmapname;
    private String jobtemplateconfigmapkey;
    private String jobname;

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public long getChecksqsintervallms() { return checksqsintervallms; }
    public void setChecksqsintervallms(long checksqsintervallms) { this.checksqsintervallms = checksqsintervallms; }

    public String getJobtemplateconfigmapname() { return jobtemplateconfigmapname; }
    public void setJobtemplateconfigmapname(String jobtemplateconfigmapname) { this.jobtemplateconfigmapname = jobtemplateconfigmapname; }

    public String getJobtemplateconfigmapkey() { return jobtemplateconfigmapkey; }
    public void setJobtemplateconfigmapkey(String jobtemplateconfigmapkey) { this.jobtemplateconfigmapkey = jobtemplateconfigmapkey; }

    public String getJobname() { return jobname; }
    public void setJobname(String jobname) { this.jobname = jobname; }
}