package com.ai.agent.backend.model;

public final class BedrockImageGeneratorResponse {
    private String imageBase64;
    private String contentType;

    private BedrockImageGeneratorResponse() {
    }

    public static BedrockImageGeneratorResponse.BedrockImageGeneratorResponseBuilder builder() {
        return new BedrockImageGeneratorResponse.BedrockImageGeneratorResponseBuilder();
    }

    public static final class BedrockImageGeneratorResponseBuilder {
        private String imageBase64;
        private String contentType;

        private BedrockImageGeneratorResponseBuilder() {
        }

        public BedrockImageGeneratorResponse.BedrockImageGeneratorResponseBuilder imageBase64(String imageBase64) {
            this.imageBase64 = imageBase64;
            return this;
        }

        public BedrockImageGeneratorResponse.BedrockImageGeneratorResponseBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public BedrockImageGeneratorResponse build() {
            BedrockImageGeneratorResponse response = new BedrockImageGeneratorResponse();
            response.imageBase64 = this.imageBase64;
            response.contentType = this.contentType;
            return response;
        }
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public String getContentType() {
        return contentType;
    }   
}
