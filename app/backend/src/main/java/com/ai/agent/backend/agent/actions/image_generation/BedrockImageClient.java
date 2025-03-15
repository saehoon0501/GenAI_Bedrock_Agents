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

@Component
public class BedrockImageClient {

    private final BedrockRuntimeClient bedrockRuntimeClient;
    private final String MODEL_ID = "stable-diffusion-v2.1";

    public BedrockImageClient(BedrockRuntimeClient bedrockRuntimeClient) {
        this.bedrockRuntimeClient = bedrockRuntimeClient;
    }
        
    public BedrockImageGeneratorResponse generateImage(String prompt) {
        return generateImage(prompt, "comic-book", generateRandomSeed());
    }
    
    public BedrockImageGeneratorResponse generateImage(String prompt, String style, BigInteger seed) {
        // The InvokeModel API uses the model's native payload format
        String nativeRequest = """
                {
                    "text_prompts": [{ "text": "%s" }],
                    "style_preset": "%s",
                    "seed": %s
                }""".formatted(prompt, style, seed.toString());
                
        try {
            // Encode and send the request to the Bedrock Runtime
            var response = bedrockRuntimeClient.invokeModel(request -> request
                    .body(SdkBytes.fromUtf8String(nativeRequest))
                    .modelId(MODEL_ID)
            );

            // Decode the response body
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseBody = objectMapper.readTree(response.body().asUtf8String());

            // Retrieve the generated image data from the model's response
            String base64ImageData = responseBody.at("/artifacts/0/base64").asText();
            String contentType = response.contentType();

            return BedrockImageGeneratorResponse.builder()
                .imageUrl(base64ImageData)
                .contentType(contentType)
                .build();

        } catch (JsonProcessingException e) {
            System.err.printf("ERROR: Failed to parse response. Reason: %s%n", e.getMessage());
            throw new RuntimeException("Failed to parse image generation response", e);
        } catch (SdkClientException e) {
            System.err.printf("ERROR: Can't invoke '%s'. Reason: %s%n", MODEL_ID, e.getMessage());
            throw new RuntimeException("Failed to generate image", e);
        }
    }
    
    private BigInteger generateRandomSeed() {
        // Get a random 32-bit seed for the image generation (max. 4,294,967,295)
        return new BigInteger(31, new SecureRandom());
    }
}