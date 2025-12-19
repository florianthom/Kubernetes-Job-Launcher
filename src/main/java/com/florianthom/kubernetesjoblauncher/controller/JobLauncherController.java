package com.florianthom.kubernetesjoblauncher.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.florianthom.kubernetesjoblauncher.services.JobLauncherService;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import tools.jackson.databind.json.JsonMapper;


@RestController("/")
public class JobLauncherController {

    @Value("${kubernetesjoblauncher.queue.inputqueueurl}")
    private String inputQueueUrl;
    private final JobLauncherService jobLauncherService;
    public record ExampleJobRequestDto(String jobId, String title) {}
    private final JsonMapper objectMapper;
    private final SqsClient sqsClient;

    public JobLauncherController(JobLauncherService jobLauncherService, JsonMapper objectMapper, SqsClient sqsClient){
        this.jobLauncherService = jobLauncherService;
        this.objectMapper = objectMapper;
        this.sqsClient = sqsClient;
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

    @GetMapping("/put-sqsmessage-in-input-queue-if-available")
    public ResponseEntity<String> putSqsMessage() throws ApiException, JsonProcessingException {
        sqsClient.sendMessage(
                SendMessageRequest.builder()
                    .queueUrl(inputQueueUrl).messageBody(objectMapper.writeValueAsString(new ExampleJobRequestDto("jobid-123", "jobtitle")))
                    .build()
        );
        return ResponseEntity.ok("Wrote message into sqs");
    }
}
