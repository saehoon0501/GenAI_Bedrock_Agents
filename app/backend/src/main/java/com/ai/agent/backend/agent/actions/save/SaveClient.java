package com.ai.agent.backend.agent.actions.save;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import com.ai.agent.backend.agent.actions.AgentAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;

@Component
public class SaveClient extends AgentAction implements Save<PutObjectResponse> {

    private static final Logger logger = LoggerFactory.getLogger(SaveClient.class);

    private final S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public SaveClient(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public PutObjectResponse handleSaveStringContent( String key, String content, String contentType) {
        
        logger.info("Saving content to S3: length={}, key={}, contentType={}, bucketName={}", 
                content.length(), key, contentType, bucketName);
        
        try {
            // Use explicit UTF-8 encoding
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(contentType)
                .key(key)
                .build();
            
            logger.info("S3 request details: bucket={}, key={}, contentType={}", 
                    request.bucket(), request.key(), request.contentType());
                
            return s3Client.putObject(request, RequestBody.fromBytes(contentBytes));
        } catch (Exception e) {
            logger.error("Error saving content to S3", e);
            throw e;
        }
    }

    @Override
    public PutObjectResponse handleSaveByteContent(
            @RequestParam("key") String key, 
            @RequestParam("content") byte[] content, 
            @RequestParam("contentType") String contentType) {
        
        logger.info("Saving binary content to S3: length={}, key={}, contentType={}, bucketName={}", 
                content.length, key, contentType, bucketName);
        
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(contentType)
                .key(key)
                .build();
            
            logger.info("S3 request details: bucket={}, key={}, contentType={}", 
                    request.bucket(), request.key(), request.contentType());
                
            return s3Client.putObject(request, RequestBody.fromBytes(content));
        } catch (Exception e) {
            logger.error("Error saving binary content to S3", e);
            throw e;
        }
    }
}
