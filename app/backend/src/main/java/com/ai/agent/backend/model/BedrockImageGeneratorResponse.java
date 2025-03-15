package com.ai.agent.backend.model;

public final class BedrockImageGeneratorResponse {
    private String imageUrl;
    private String contentType;

    private BedrockImageGeneratorResponse() {
    }

    public static BedrockImageGeneratorResponse.BedrockImageGeneratorResponseBuilder builder() {
        return new BedrockImageGeneratorResponse.BedrockImageGeneratorResponseBuilder();
    }

    public static final class BedrockImageGeneratorResponseBuilder {
        private String imageUrl;
        private String contentType;

        private BedrockImageGeneratorResponseBuilder() {
        }

        public BedrockImageGeneratorResponse.BedrockImageGeneratorResponseBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public BedrockImageGeneratorResponse.BedrockImageGeneratorResponseBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public BedrockImageGeneratorResponse build() {
            BedrockImageGeneratorResponse response = new BedrockImageGeneratorResponse();
            response.imageUrl = this.imageUrl;
            response.contentType = this.contentType;
            return response;
        }
    }
}
