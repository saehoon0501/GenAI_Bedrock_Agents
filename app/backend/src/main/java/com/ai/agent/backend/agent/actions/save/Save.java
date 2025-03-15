package com.ai.agent.backend.agent.actions.save;

import com.ai.agent.backend.model.SaveResponse;

public interface Save {
    SaveResponse handleSaveStringContent(String key, String content, String contentType);
    SaveResponse handleSaveByteContent(String key, byte[] content, String contentType);
}
