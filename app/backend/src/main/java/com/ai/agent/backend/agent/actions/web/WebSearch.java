package com.ai.agent.backend.agent.actions.web;

public interface WebSearch <I, O>{
    O search(I query);
}
