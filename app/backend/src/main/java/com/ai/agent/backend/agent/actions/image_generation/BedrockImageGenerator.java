package com.ai.agent.backend.agent.actions.image_generation;

import org.springframework.stereotype.Component;
import com.ai.agent.backend.agent.actions.AgentAction;
import com.ai.agent.backend.model.BedrockImageGeneratorResponse;
import com.ai.agent.backend.agent.actions.save.SaveClient;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@Component
public class BedrockImageGenerator extends AgentAction implements ImageGenerator {

    private final BedrockImageClient bedrockImageClient;
    private final SaveClient saveClient;
    private final Logger logger = LoggerFactory.getLogger(BedrockImageGenerator.class);
    
    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    private final String IMAGE_PATH_PREFIX = "images/";

    public BedrockImageGenerator(BedrockImageClient bedrockImageClient, SaveClient saveClient) {
        this.bedrockImageClient = bedrockImageClient;
        this.saveClient = saveClient;
    }

    @Override
    public String handleGenerateImage(String prompt) {
        try {
            BedrockImageGeneratorResponse imageResponse = bedrockImageClient.generateImage(prompt);            
            String imageKey = generateImageToken();
            
            // The base64 data from Bedrock might include a data URL prefix, remove it if present
            String base64Data = imageResponse.getImageBase64();
            if (base64Data.startsWith("data:")) {
                base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
            }
            
            // Decode the base64 string to binary data
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            
            // Save the binary data instead of the raw base64 string
            var result = saveClient.handleSaveByteContent(imageKey, imageBytes, imageResponse.getContentType());

            if(result.getStatusCode() != 200) {
                throw new RuntimeException("Failed to save image to S3");
            }        
            
            // Return the URL from the SaveResponse
            return result.getUrl();
        } catch (Exception e) {
            logger.error("Error generating image: {}", e.getMessage());
            throw new RuntimeException("Failed to generate image", e);
        }
    }
 
    private String generateImageToken() {
        String token = UUID.randomUUID().toString();
        return IMAGE_PATH_PREFIX + token;
    }
}