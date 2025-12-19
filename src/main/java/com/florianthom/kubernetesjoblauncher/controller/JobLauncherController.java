package com.florianthom.kubernetesjoblauncher.controller;

import com.florianthom.kubernetesjoblauncher.services.JobLauncherService;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("/")
public class JobLauncherController {

    private final JobLauncherService jobLauncherService;

    public JobLauncherController(JobLauncherService jobLauncherService){
        this.jobLauncherService = jobLauncherService;
    }

    @GetMapping("")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("<h1>ping</h1>");
    }

    @GetMapping("/launchjob")
    public ResponseEntity<String> launchJob() throws ApiException {

        var launchedJob = jobLauncherService.launchJob();
        return ResponseEntity.ok("Job launched: " + launchedJob.getMetadata().getName());
    }
}
