package com.ai.agent.backend.agent.actions.image_generation;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ai.agent.backend.model.BedrockImageGeneratorResponse;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

@Component
public class BedrockImageClient {

    private final BedrockRuntimeClient bedrockRuntimeClient;    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(BedrockImageClient.class);

    // Stable Diffusion model ID
    private final String MODEL_ID = "stability.stable-image-core-v1:1";

    public BedrockImageClient(BedrockRuntimeClient bedrockRuntimeClient) {
        this.bedrockRuntimeClient = bedrockRuntimeClient;        
    }
        
    public BedrockImageGeneratorResponse generateImage(String prompt) throws Exception {
        return generateImage(prompt, null, generateRandomSeed());
    }
    
    public BedrockImageGeneratorResponse generateImage(String prompt, String style, BigInteger seed) throws Exception {
        // Format the request using the correct format for Stable Image Core
        String requestBody = """
            {
              "prompt": "%s",
              "seed": %d
            }
            """.formatted(
                prompt.replace("\"", "\\\""),
                seed
            );
        
        logger.info("Sending request to Bedrock: {}", requestBody);
        
        // Encode and send the request to the Bedrock Runtime
        InvokeModelResponse response = bedrockRuntimeClient.invokeModel(request -> request
                .body(SdkBytes.fromUtf8String(requestBody))
                .modelId(MODEL_ID)
                .accept("application/json")
                .contentType("application/json")
        );

        // Decode the response body
        String responseBodyStr = response.body().asUtf8String();            
        JsonNode responseBody = objectMapper.readTree(responseBodyStr);
                
        if (responseBody.has("images") && responseBody.get("images").isArray() && 
            responseBody.get("images").size() > 0) {
            
            String base64ImageData = responseBody.get("images").get(0).asText();
            String contentType = "image/png"; // Default for Stable Image Core            
            
            return BedrockImageGeneratorResponse.builder()
                .imageBase64(base64ImageData)
                .contentType(contentType)
                .build();                    
        } else {
            throw new RuntimeException("Unexpected response structure from Bedrock API");
        }
    }
    
    private BigInteger generateRandomSeed() {
        // Get a random 32-bit seed for the image generation (max. 4,294,967,295)
        return new BigInteger(31, new SecureRandom());
    }
}