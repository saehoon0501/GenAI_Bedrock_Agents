package com.ai.agent.backend.utility;

import java.util.ArrayList;
import java.util.List;
import java.security.InvalidParameterException;

import com.ai.agent.backend.constant.enums.ActionGroup;
import com.ai.agent.backend.constant.enums.OperationId;
import software.amazon.awssdk.services.bedrockagentruntime.model.*;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BedrockUtils {    

    private static List<Parameter> getApiParameters(List<InvocationInputMember> payload) throws InvalidParameterException {
        List<Parameter> parameters = new ArrayList<>();
        
            // Extract properties directly from the PropertyParameters
            if (payload.get(0).apiInvocationInput().requestBody().content().containsKey("application/json")) {
                parameters = payload.get(0).apiInvocationInput().requestBody()
                    .content().get("application/json").properties();                
            }else{
                throw new InvalidParameterException("Invalid payload");
            }                                              

        return parameters;
    }

    public static ActionGroup getActionGroup(List<InvocationInputMember> payload) {
        String actionGroupName = payload.get(0).apiInvocationInput().actionGroup();
        return ActionGroup.valueOf(actionGroupName);               
    }

    public static OperationId getOperationId(List<InvocationInputMember> payload) {
        List<Parameter> parameters = getApiParameters(payload);
        return parameters.stream()
                .filter(
                    parameter -> parameter.name().equals("operationId")
                )
                .findFirst()
                .map(Parameter::value)
                .map(OperationId::valueOf)
                .orElseThrow(() -> new InvalidParameterException("OperationId not found"));
    }

    public static List<Parameter> getFunctionParameters(List<InvocationInputMember> payload) {
        List<Parameter> parameters = getApiParameters(payload);
        return parameters.stream()
                .filter(
                    parameter -> !parameter.name().equals("operationId")
                )
                .collect(Collectors.toList());
    }

    /**
     * Creates a session state map based on the ReturnControlPayload event
     * Format matches:
     * {
     *   "invocationId": "337cb2f6-ec74-4b49-8141-00b8091498ad",
     *   "returnControlInvocationResults": [{
     *     "apiResult": {
     *       "actionGroup": "WeatherAPIs",
     *       "httpMethod": "get",
     *       "apiPath": "/get-weather",
     *       "responseBody": {
     *         "application/json": {
     *           "body": "It's rainy in Seattle today."
     *         }
     *       }
     *     }
     *   }]
     * }
     * 
     * @param event The ReturnControlPayload event
     * @return A map representing the session state as strings
     */
    public static SessionState getSessionStateMap(ReturnControlPayload event, String result) {         
        SessionState sessionState = null;
        try {                                    
            // Get payload information
            List<InvocationInputMember> payload = event.invocationInputs();
            if (payload != null && !payload.isEmpty()) {                
                
                // Get API information
                String actionGroup = payload.get(0).apiInvocationInput().actionGroup();
                String httpMethod = payload.get(0).apiInvocationInput().httpMethod();
                // String apiPath = payload.get(0).apiInvocationInput().apiPath();                                          
                
                // Create responseBody structure
                Map<String, ContentBody> responseBody = new HashMap<>();
                
                ContentBody applicationJson = ContentBody.builder()
                                                            .body(result)
                                                            .build();

                responseBody.put("application/json", applicationJson);

                ApiResult apiResult = ApiResult.builder()
                                                .actionGroup(actionGroup)
                                                .httpMethod(httpMethod)
                                                .apiPath("/api/web/search")   
                                                .responseBody(responseBody)       
                                                .responseState(ResponseState.REPROMPT)
                                                .build();
                
                InvocationResultMember invocationResultMember = InvocationResultMember.builder()
                                                                                        .apiResult(apiResult)
                                                                                        .build();

                sessionState = SessionState.builder()                    
                    .invocationId(event.invocationId())
                    .returnControlInvocationResults(invocationResultMember)
                    .build();                
            }
            
            return sessionState;
        } catch (Exception e) {
            // If there's an error, add a simple placeholder
            throw new RuntimeException("Error creating session state map: " + e.getMessage());
        }                
    }
}
