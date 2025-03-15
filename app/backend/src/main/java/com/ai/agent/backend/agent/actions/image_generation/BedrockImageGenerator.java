package com.ai.agent.backend.agent.actions.image_generation;

import org.springframework.stereotype.Component;
import com.ai.agent.backend.agent.actions.AgentAction;
import com.ai.agent.backend.model.BedrockImageGeneratorResponse;

@Component
public class BedrockImageGenerator extends AgentAction implements ImageGenerator {

    private final BedrockImageClient bedrockImageClient;

    public BedrockImageGenerator(BedrockImageClient bedrockImageClient) {
        this.bedrockImageClient = bedrockImageClient;
    }

    @Override
    public BedrockImageGeneratorResponse handleGenerateImage(String prompt) {
        return bedrockImageClient.generateImage(prompt);
    }
    
}