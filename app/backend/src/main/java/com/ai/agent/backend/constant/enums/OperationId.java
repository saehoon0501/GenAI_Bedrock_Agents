package com.ai.agent.backend.constant.enums;

public enum OperationId {
    SEARCH_WEB_CONTENT("/web/search"),

    GENERATE_IMAGE("/image/generate"),
    
    SAVE_STRING_CONTENT("/save/string-content"),
    
    SAVE_BYTE_CONTENT("/save/byte-content"),

    UNKNOWN("");

    private final String value;

    OperationId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
