package com.ai.agent.backend.agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Async;

import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockagentruntime.model.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.security.InvalidParameterException;
import java.util.concurrent.ConcurrentHashMap;

import com.ai.agent.backend.utility.BedrockUtils;
import com.ai.agent.backend.agent.actions.AgentActionFactory;
import com.ai.agent.backend.constant.enums.ActionGroup;
import com.ai.agent.backend.agent.actions.AgentAction;
import com.ai.agent.backend.constant.enums.OperationId;
import com.ai.agent.backend.model.AgentResponse;


@Component
public class AgentService {

    @Value("${aws.bedrock.web.agent.id}")
    String agentId;

    @Value("${aws.bedrock.web.agent.alias.id}")
    String aliasId;

    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);    
    private final BedrockAgentRuntimeAsyncClient client;
    private final AgentActionFactory agentActionFactory;
    private String sessionId;

    private final Map<String, String> sessionMap = new ConcurrentHashMap<>();

    public AgentService(BedrockAgentRuntimeAsyncClient client, AgentActionFactory agentActionFactory) {
        this.client = client;
        this.agentActionFactory = agentActionFactory;
    }

    public void invokeAgent(String input) {
        sessionId = UUID.randomUUID().toString();

        InvokeAgentRequest invokeAgentRequest = InvokeAgentRequest.builder()
                .agentId(agentId)
                .agentAliasId(aliasId)
                .sessionId(sessionId)
                .enableTrace(true)
                .inputText(input)
                .build();

        InvokeAgentResponseHandler handler = InvokeAgentResponseHandler.builder()
                .onResponse(response -> {
                    logger.info("Response Received from Agent: {}", response.toString());
                    // Process the response here
                })
                .onEventStream(publisher -> publisher.subscribe(event -> handleEvent(event, sessionId)))
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

    @Async("agentTaskExecutor")
    public CompletableFuture<Void> invokeAgentAsync(String invocationId, SessionState sessionState) {        
        InvokeAgentRequest invokeAgentRequest = InvokeAgentRequest.builder()
                .agentId(agentId)
                .agentAliasId(aliasId)
                .sessionId(sessionMap.get(invocationId))
                .sessionState(sessionState)
                .build();

        sessionMap.remove(invocationId);

        InvokeAgentResponseHandler handler = InvokeAgentResponseHandler.builder()
            .onResponse(response -> {
                logger.info("Response Received from Agent: {}", response.toString());
                // Process the response here
            })
            .onEventStream(publisher -> publisher.subscribe(event -> handleEvent(event, sessionMap.get(invocationId))))
            .onError(error -> {
                logger.error("Error occurred: ", error);
            })
            .build();

        // Return the CompletableFuture directly instead of blocking
        return client.invokeAgent(invokeAgentRequest, handler)
            .orTimeout(300, TimeUnit.SECONDS)
            .exceptionally(ex -> {
                logger.error("Error in async agent invocation: ", ex);
                return null;
            });
    }

    private void handleEvent(ResponseStream event, String sessionId) {
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
                sessionMap.put(event.invocationId(), sessionId);
                var payload = event.invocationInputs();                
                if (payload.isEmpty() ||
                    payload.get(0).apiInvocationInput().requestBody().content() == null) {                                
                        throw new InvalidParameterException("Payload is null");
                }
                try{
                    ActionGroup actionGroup = BedrockUtils.getActionGroup(payload);
                    OperationId operationId = BedrockUtils.getOperationId(payload);
                    List<Parameter> functionParameters = BedrockUtils.getFunctionParameters(payload);
                    
                    AgentAction action = agentActionFactory.createAction(actionGroup);
                    AgentResponse jsonStringResult = action.execute(operationId, functionParameters);

                    logger.info("{} API Result: {}", operationId, jsonStringResult);

                    if(jsonStringResult != null){
                        // Build the SessionState object
                        SessionState sessionState = BedrockUtils.getSessionStateMap(event, jsonStringResult);
                        // Call the async method without waiting for it to complete
                        invokeAgentAsync(event.invocationId(), sessionState);
                    }
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
