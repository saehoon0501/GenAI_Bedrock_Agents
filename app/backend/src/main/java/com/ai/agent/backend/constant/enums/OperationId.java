package com.ai.agent.backend.constant.enums;

public enum OperationId {
    SEARCH_WEB_CONTENT("/web/search"),

    GENERATE_IMAGE("/image/generate"),
    
    SAVE_CONTENT("/save/content"),

    UNKNOWN("");

    private final String value;

    OperationId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
