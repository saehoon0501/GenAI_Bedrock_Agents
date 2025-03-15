package com.ai.agent.backend.model;

/**
 * A serializable response object for save operations.
 * This replaces the direct use of AWS SDK's PutObjectResponse which can't be serialized.
 */
public class SaveResponse {
    private String bucket;
    private String key;
    private String url;
    private int statusCode;
    private String requestId;

    public SaveResponse() {
        // Default constructor for Jackson
    }

    public static SaveResponseBuilder builder() {
        return new SaveResponseBuilder();
    }

    public String getBucket() {
        return bucket;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public static class SaveResponseBuilder {
        private final SaveResponse response = new SaveResponse();

        public SaveResponseBuilder bucket(String bucket) {
            response.bucket = bucket;
            return this;
        }

        public SaveResponseBuilder key(String key) {
            response.key = key;
            return this;
        }

        public SaveResponseBuilder url(String url) {
            response.url = url;
            return this;
        }

        public SaveResponseBuilder statusCode(int statusCode) {
            response.statusCode = statusCode;
            return this;
        }

        public SaveResponseBuilder requestId(String requestId) {
            response.requestId = requestId;
            return this;
        }

        public SaveResponse build() {
            return response;
        }
    }
} 