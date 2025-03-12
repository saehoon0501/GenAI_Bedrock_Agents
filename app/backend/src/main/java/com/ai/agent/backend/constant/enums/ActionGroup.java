package com.ai.agent.backend.constant.enums;

public enum ActionGroup {
    WEB_SEARCH("web_search"),
    WRITER("writer");

    private final String value;

    ActionGroup(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
