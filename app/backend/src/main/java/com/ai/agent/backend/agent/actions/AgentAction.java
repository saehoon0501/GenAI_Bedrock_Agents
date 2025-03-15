package com.ai.agent.backend.agent.actions;

import java.util.List;

import com.ai.agent.backend.constant.enums.OperationId;
import com.ai.agent.backend.model.AgentResponse;
import com.ai.agent.backend.open_api.method.OpenApiMethodInvoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import software.amazon.awssdk.services.bedrockagentruntime.model.Parameter;

import java.lang.reflect.Method;

/**
 * Abstract base class for agent actions.
 * Provides common functionality for executing actions based on operation IDs.
 * For example, if the operation ID is SEARCH_WEB_CONTENT, the matching action will be handleSearchWebContent.
 * 
 * @param <T> The type of result produced by this action.
 */
public abstract class AgentAction {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentAction.class);
    
    @Autowired
    protected ConversionService conversionService;
    
    @Autowired
    protected OpenApiMethodInvoker methodInvoker;
    
    /**
     * Execute the action based on the operation ID and parameters.
     * 
     * @param operationId The operation ID
     * @param parameters The parameters for the operation
     * @return The agent response
     */
    public AgentResponse execute(OperationId operationId, List<Parameter> parameters) {
        try {
            // Try to find a handler method with @OpenApiRequest annotations
            Method method = findHandlerMethod(operationId);
            if (method != null) {
                // Invoke the method with automatic parameter resolution
                return AgentResponse.stringifyJson(methodInvoker.invokeMethod(this, method, parameters));
            }
            throw new IllegalArgumentException("No handler method found for operation ID: " + operationId);
        } catch (Exception e) {
            logger.error("Error executing agent action", e);
            return AgentResponse.stringifyJson("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Find a handler method for the given operation ID.
     * This looks for a method named "handle" + operationId (e.g., handleSearchWebContent)
     * or a method with a matching OperationId in its name.
     * 
     * @param operationId The operation ID to find a handler for
     * @return The handler method, or null if none found
     */
    private Method findHandlerMethod(OperationId operationId) {
        String operationIdName = operationId.name();
        
        // Convert SEARCH_WEB_CONTENT to SearchWebContent
        StringBuilder camelCaseName = new StringBuilder();
        String[] parts = operationIdName.split("_");
        for (String part : parts) {
            if (part.length() > 0) {
                camelCaseName.append(part.substring(0, 1).toUpperCase());
                camelCaseName.append(part.substring(1).toLowerCase());
            }
        }
        
        String handlerMethodName = "handle" + camelCaseName.toString();
        
        // Try with the convention-based method name
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.getName().equals(handlerMethodName)) {
                return method;
            }
        }
        
        return null;
    }    
}
