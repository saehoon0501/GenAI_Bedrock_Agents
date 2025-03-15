package com.ai.agent.backend.open_api.method;

import com.ai.agent.backend.open_api.parameter.OpenApiRequestResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.services.bedrockagentruntime.model.Parameter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for invoking methods that have parameters annotated with @OpenApiRequest.
 * This service handles the parameter resolution and method invocation process.
 */
@Service
public class OpenApiMethodInvoker {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiMethodInvoker.class);
    private final OpenApiRequestResolver openApiRequestResolver;
    
    public OpenApiMethodInvoker(OpenApiRequestResolver openApiRequestResolver) {
        this.openApiRequestResolver = openApiRequestResolver;
    }
    
    /**
     * Get parameter name using multiple strategies, including annotation-based discovery.
     */
    private String getParameterName(MethodParameter methodParam, int paramIndex) {
        String paramName = methodParam.getParameterName();
        
        // If parameter name is available from reflection, use it
        if (paramName != null) {
            logger.info("Parameter name from reflection: {}", paramName);
            return paramName;
        }        
        
        // Try Java 8's Parameter API as a fallback
        try {
            Method method = methodParam.getMethod();
            java.lang.reflect.Parameter[] parameters = method.getParameters();
            if (paramIndex < parameters.length && parameters[paramIndex].isNamePresent()) {
                paramName = parameters[paramIndex].getName();
                logger.info("Parameter name from Java Parameter API: {}", paramName);
                return paramName;
            }
        } catch (Exception e) {
            logger.warn("Failed to get parameter name using Java Parameter API", e);
        }
        
        // Use positional fallback
        logger.warn("Could not determine parameter name, using position as fallback: param{}", paramIndex);
        return "param" + paramIndex;
    }
    
    /**
     * Invoke a method with parameters, resolving any @OpenApiRequest annotated parameters.
     * 
     * @param target the target object on which to invoke the method
     * @param method the method to invoke
     * @param parameters the list of AWS Bedrock Parameters
     * @param args additional arguments to pass to the method (will be matched by position)
     * @return the result of the method invocation
     * @throws Exception if an error occurs during invocation
     */
    public Object invokeMethod(Object target, Method method, List<Parameter> parameters, Object... args) throws Exception {
        // Prepare arguments for the method
        Class<?>[] paramTypes = method.getParameterTypes();        
        Object[] methodArgs = new Object[paramTypes.length];
        
        // Debug method signature
        logger.info("Invoking method: {}.{}(...)", 
            method.getDeclaringClass().getSimpleName(), method.getName());
        
        // Create a map of parameter name to parameter value
        Map<String, Parameter> parameterMap = new HashMap<>();
        for (Parameter param : parameters) {
            parameterMap.put(param.name(), param);
            logger.info("Available parameter from API: {}", param.name());
        }
        
        // Initialize with null values
        for (int i = 0; i < methodArgs.length; i++) {
            methodArgs[i] = null;
        }
        
        // Fill in the additional arguments first
        for (int i = 0; i < args.length && i < methodArgs.length; i++) {
            methodArgs[i] = args[i];
        }        
        logger.info("Method Args: {}", methodArgs.length);
        
        // Process @OpenApiRequest annotations and resolve parameters
        for (int i = 0; i < paramTypes.length; i++) {
            MethodParameter methodParam = new MethodParameter(method, i);
            methodParam.initParameterNameDiscovery(null); // Initialize parameter name discovery
            
            String paramName = getParameterName(methodParam, i);
            
            logger.info("Parameter at position {}: name={}, type={}", 
                i, paramName, methodParam.getParameterType().getName());
            
            // If argument already provided, skip
            if (methodArgs[i] != null) {
                logger.info("Argument already provided at position {}, skipping", i);
                continue;
            }
            
            // Find parameter by name if available
            Parameter openApiParameter = null;
            if (paramName != null && parameterMap.containsKey(paramName)) {
                openApiParameter = parameterMap.get(paramName);
                logger.info("Found parameter by name: {} -> {}", paramName, openApiParameter.name());
            } else if (i < parameters.size()) {
                // Fallback to position-based matching if name not found
                openApiParameter = parameters.get(i);
                logger.info("Using position-based parameter matching for position {}: {}", 
                    i, openApiParameter.name());
            } else {
                logger.warn("No parameter found for position {}, parameter name: {}", i, paramName);
            }
            
            if (openApiParameter != null) {
                // Resolve the parameter
                methodArgs[i] = openApiRequestResolver.resolveArgument(methodParam, openApiParameter);
                logger.info("Resolved parameter {} to {}", paramName, methodArgs[i]);
            } else {
                logger.warn("No API parameter available for method parameter at position {}: {}", 
                    i, paramName);
            }
        }

        // Invoke the method
        logger.info("Invoking method with {} arguments", methodArgs.length);
        return method.invoke(target, methodArgs);
    }
} 