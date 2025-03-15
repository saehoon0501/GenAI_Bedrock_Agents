package com.ai.agent.backend.agent.actions.image_generation;

import com.ai.agent.backend.model.BedrockImageGeneratorResponse;

public interface ImageGenerator {
    BedrockImageGeneratorResponse handleGenerateImage(String prompt);
}
