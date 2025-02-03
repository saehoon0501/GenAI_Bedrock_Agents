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



@Component
public class AgentService {

    @Value("${aws.bedrock.agent.id}")
    String agentId;

    @Value("${aws.bedrock.agent.alias.id}")
    String aliasId;

    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final BedrockAgentRuntimeAsyncClient client;

    public AgentService(BedrockAgentRuntimeAsyncClient client) {
        this.client = client;
    }

    public void generateContent(String input) {

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
                logger.info("[visitReturnControl] - {}", event.invocationInputs());
                extractParameters(event.invocationInputs());
            }

            @Override
            public void visitTrace(TracePart event) {
                logger.info("[visitTrace] - {}", event.toString());
            }
        });
    }

    public static Map<String, String> extractParameters(List<InvocationInputMember> payload) {
        Map<String, String> extractedParams = new HashMap<>();

//        try {
            // Convert payload bytes to JSON string
            String jsonPayload = payload.get(0).apiInvocationInput().requestBody().content().toString();
            logger.info("Received ReturnControlPayload: {}", payload.get(0).apiInvocationInput().requestBody().content());

            // Parse JSON
//            JsonNode rootNode = objectMapper.readTree(jsonPayload);

//            // Extract key-value parameters
//            JsonNode parametersNode = rootNode.path("parameters");
//            if (parametersNode.isObject()) {
//                Iterator<Map.Entry<String, JsonNode>> fields = parametersNode.fields();
//                while (fields.hasNext()) {
//                    Map.Entry<String, JsonNode> entry = fields.next();
//                    extractedParams.put(entry.getKey(), entry.getValue().asText());
//                }
//            }

//        } catch (IOException e) {
//            logger.error("Error parsing ReturnControlPayload", e);
//        }

        return extractedParams;
    }
}
