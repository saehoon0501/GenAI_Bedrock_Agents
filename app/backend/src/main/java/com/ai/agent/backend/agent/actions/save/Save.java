package com.ai.agent.backend.agent.actions.save;

public interface Save <T> {
    T handleSaveContent(String key, String content, String contentType);
    T handleSaveContent(String key, byte[] content, String contentType);
}
