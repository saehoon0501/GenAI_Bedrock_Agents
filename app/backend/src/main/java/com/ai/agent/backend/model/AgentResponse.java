package com.ai.agent.backend.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public final class AgentResponse {
    private final String jsonResultString;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private AgentResponse(String jsonResultString) {
        this.jsonResultString = jsonResultString;
    }

    public static AgentResponse stringifyJson(Object object) {
        try {
            return new AgentResponse(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    public String getJsonResultString() {
        return jsonResultString;
    }
        
}
