package com.ai.agent.backend.agent.actions.save;

public interface Save <T> {
    T handleSaveStringContent(String key, String content, String contentType);
    T handleSaveByteContent(String key, byte[] content, String contentType);
}
