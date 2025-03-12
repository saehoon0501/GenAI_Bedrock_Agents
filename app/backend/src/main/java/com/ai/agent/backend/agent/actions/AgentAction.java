package com.ai.agent.backend.agent.actions;

import java.util.List;

import com.ai.agent.backend.constant.enums.OperationId;

import software.amazon.awssdk.services.bedrockagentruntime.model.Parameter;

public interface AgentAction {
    public void execute(OperationId operationId, List<Parameter> parameters);
}
