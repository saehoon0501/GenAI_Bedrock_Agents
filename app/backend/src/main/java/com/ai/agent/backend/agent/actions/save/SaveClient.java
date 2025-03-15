package com.ai.agent.backend.agent.actions.save;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import com.ai.agent.backend.agent.actions.AgentAction;
import com.ai.agent.backend.model.SaveResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;

@Component
public class SaveClient extends AgentAction implements Save {

    private static final Logger logger = LoggerFactory.getLogger(SaveClient.class);

    private final S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    
    @Value("${aws.region}")
    private String region;

    public SaveClient(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public SaveResponse handleSaveStringContent(String key, String content, String contentType) {
        
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
                
            PutObjectResponse awsResponse = s3Client.putObject(request, RequestBody.fromBytes(contentBytes));
            
            // Convert AWS response to our serializable response
            String url = constructS3Url(key);
            
            return SaveResponse.builder()
                .bucket(bucketName)
                .key(key)
                .url(url)
                .statusCode(awsResponse.sdkHttpResponse().statusCode())
                .requestId(awsResponse.responseMetadata().requestId())
                .build();
        } catch (Exception e) {
            logger.error("Error saving content to S3", e);
            throw e;
        }
    }

    @Override
    public SaveResponse handleSaveByteContent(
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
                
            PutObjectResponse awsResponse = s3Client.putObject(request, RequestBody.fromBytes(content));
            
            // Convert AWS response to our serializable response
            String url = constructS3Url(key);
            
            return SaveResponse.builder()
                .bucket(bucketName)
                .key(key)
                .url(url)
                .statusCode(awsResponse.sdkHttpResponse().statusCode())
                .requestId(awsResponse.responseMetadata().requestId())
                .build();
        } catch (Exception e) {
            logger.error("Error saving binary content to S3", e);
            throw e;
        }
    }
    
    /**
     * Constructs the S3 URL for the saved object
     */
    private String constructS3Url(String key) {
        if (region != null && !region.equals("us-east-1")) {
            // For regions other than us-east-1, include the region in the URL
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
        } else {
            // For us-east-1 or if region is null, use the global endpoint
            return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
        }
    }
}
