package com.ai.agent.backend.open_api.method;

import com.ai.agent.backend.open_api.parameter.OpenApiRequestResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.bedrockagentruntime.model.Parameter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

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
        logger.info("paramTypes: {}", Arrays.toString(paramTypes));
        Object[] methodArgs = new Object[paramTypes.length];
        
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
            Parameter openApiParameter = parameters.get(i);

            // If argument already provided, skip
            if (methodArgs[i] != null) {
                continue;
            }

            // Resolve the parameter
            methodArgs[i] = openApiRequestResolver.resolveArgument(methodParam, openApiParameter);
            logger.debug("Resolved parameter {} to {}", i, methodArgs[i]);
        }

        // Invoke the method
        return method.invoke(target, methodArgs);
    }
} 