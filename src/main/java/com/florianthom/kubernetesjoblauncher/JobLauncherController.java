package com.florianthom.kubernetesjoblauncher;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import tools.jackson.databind.json.JsonMapper;


@RestController("/")
public class JobLauncherController {
    private AppProperties appProperties;
    private final KubernetesjoblauncherApplication.JobLauncherService jobLauncherService;
    public record ExampleJobRequestDto(String jobId, String title) {}
    private final JsonMapper objectMapper;
    private final SqsClient sqsClient;

    public JobLauncherController(AppProperties appProperties, KubernetesjoblauncherApplication.JobLauncherService jobLauncherService, JsonMapper objectMapper, SqsClient sqsClient){
        this.appProperties = appProperties;
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
                    .queueUrl(appProperties.queue().inputqueueurl()).messageBody(objectMapper.writeValueAsString(
                            new ExampleJobRequestDto("jobid-123", "jobtitle"))
                    ).build()
        );
        return ResponseEntity.ok("Wrote message into sqs");
    }
}
