package com.ai.agent.backend.agent.actions.save;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import com.ai.agent.backend.agent.actions.AgentAction;
import org.springframework.beans.factory.annotation.Value;

@Component
public class SaveClient extends AgentAction implements Save<PutObjectResponse> {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public SaveClient(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public PutObjectResponse handleSaveContent(String key, String content, String contentType) {
        return s3Client.putObject(PutObjectRequest.builder()
            .bucket(bucketName)
            .contentType(contentType)
            .key(key)
            .build(), RequestBody.fromBytes(content.getBytes()));
    }

    @Override
    public PutObjectResponse handleSaveContent(String key, byte[] content, String contentType) {
        return s3Client.putObject(PutObjectRequest.builder()
            .bucket(bucketName)
            .contentType(contentType)
            .key(key)
            .build(), RequestBody.fromBytes(content));
    }
}
