package com.ai.agent.backend.agent.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockagentruntime.model.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.ai.agent.backend.utility.BedrockUtils;
import com.ai.agent.backend.agent.actions.AgentActionFactory;
import com.ai.agent.backend.constant.enums.ActionGroup;
import com.ai.agent.backend.agent.actions.AgentAction;
import com.ai.agent.backend.constant.enums.OperationId;

@Component
public class AgentService {

    @Value("${aws.bedrock.web.agent.id}")
    String agentId;

    @Value("${aws.bedrock.web.agent.alias.id}")
    String aliasId;

    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final BedrockAgentRuntimeAsyncClient client;
    private final AgentActionFactory agentActionFactory;

    public AgentService(BedrockAgentRuntimeAsyncClient client, AgentActionFactory agentActionFactory) {
        this.client = client;
        this.agentActionFactory = agentActionFactory;
    }

    public void invokeAgent(String input) {

        InvokeAgentRequest invokeAgentRequest = InvokeAgentRequest.builder()
                .agentId(agentId)
                .agentAliasId(aliasId)
                .sessionId(UUID.randomUUID().toString())
                .enableTrace(true)
                .inputText(input)
                .build();

        InvokeAgentResponseHandler handler = InvokeAgentResponseHandler.builder()
                .onResponse(response -> {
                    logger.info("Response Received from Agent: {}", response.toString());
                    // Process the response here
                })
                .onEventStream(publisher -> publisher.subscribe(this::handleEvent))
                .onError(error -> {
                    logger.error("Error occurred: ", error);
                })
                .build();

        CompletableFuture<Void> response = client.invokeAgent(invokeAgentRequest, handler);

        try {
            response.get(100, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("Error invoking Bedrock agent: ", e);
        }
    }

    private void handleEvent(ResponseStream event) {
        logger.info("Completion: {}", event.sdkEventType());
        logger.info("Event: {}", event.getClass().getName());

        event.accept(new InvokeAgentResponseHandler.Visitor() {
            @Override
            public void visitDefault(ResponseStream event) {
                logger.info("[visitDefault] - {}", event.toString());
            }
            @Override
            public void visitChunk(PayloadPart event) {
                logger.info("[visitChunk] - {}", event.toString());
                String payloadAsString = event.bytes().asUtf8String();
                logger.info("Chunked Data = {}", payloadAsString);
            }

            @Override
            public void visitReturnControl(ReturnControlPayload event) {
                logger.info("[visitReturnControl] - {}", event);
                var payload = event.invocationInputs();
                // if (payload.isEmpty() ||
                //     payload.get(0).apiInvocationInput().requestBody().content() == null) {                                
                //         throw new InvalidParameterException("Payload is null");
                // }
                try{
                    ActionGroup actionGroup = BedrockUtils.getActionGroup(payload);
                    OperationId operationId = BedrockUtils.getOperationId(payload);
                    List<Parameter> functionParameters = BedrockUtils.getFunctionParameters(payload);                    
                    
                    AgentAction action = agentActionFactory.createAction(actionGroup);
                    action.execute(operationId, functionParameters);
                }catch(Exception e){
                    logger.error("Error: {}", e);
                    throw new RuntimeException("Error executing action: " + e.getMessage());
                }
            }
            @Override
            public void visitTrace(TracePart event) {
                logger.info("[visitTrace] - {}", event.toString());
            }
        });
    }
}
