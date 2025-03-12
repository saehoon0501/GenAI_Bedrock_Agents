package com.ai.agent.backend.utility;

import java.util.ArrayList;
import java.util.List;
import java.security.InvalidParameterException;

import com.ai.agent.backend.constant.enums.ActionGroup;
import com.ai.agent.backend.constant.enums.OperationId;
import software.amazon.awssdk.services.bedrockagentruntime.model.*;
import java.util.stream.Collectors;
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
}
