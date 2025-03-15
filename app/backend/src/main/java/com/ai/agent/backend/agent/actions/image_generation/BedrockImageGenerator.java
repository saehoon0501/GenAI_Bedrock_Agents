package com.ai.agent.backend.agent.actions.image_generation;

import org.springframework.stereotype.Component;
import com.ai.agent.backend.agent.actions.AgentAction;
import com.ai.agent.backend.model.BedrockImageGeneratorResponse;
import com.ai.agent.backend.agent.actions.save.SaveClient;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class BedrockImageGenerator extends AgentAction implements ImageGenerator {

    private final BedrockImageClient bedrockImageClient;
    private final SaveClient saveClient;
    private final Logger logger = LoggerFactory.getLogger(BedrockImageGenerator.class);
    
    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public BedrockImageGenerator(BedrockImageClient bedrockImageClient, SaveClient saveClient) {
        this.bedrockImageClient = bedrockImageClient;
        this.saveClient = saveClient;
    }

    @Override
    public String handleGenerateImage(String prompt) {
        try {
            BedrockImageGeneratorResponse imageResponse = bedrockImageClient.generateImage(prompt);            
            String imageKey = generateImageToken();
            var result = saveClient.handleSaveStringContent(imageKey, imageResponse.getImageBase64(), imageResponse.getContentType());

            if(result.sdkHttpResponse().statusCode() != 200) {
                throw new RuntimeException("Failed to save image to S3");
            }        
            // Construct the S3 URL using the bucket name and key
            String imageUrl = "https://" + bucketName + ".s3.amazonaws.com/" + imageKey;
            return imageUrl;
        } catch (Exception e) {
            logger.error("Error generating image: {}", e.getMessage());
            throw new RuntimeException("Failed to generate image", e);
        }
    }
 
    private String generateImageToken() {
        return UUID.randomUUID().toString();
    }
}