package com.ai.agent.backend.open_api.parameter;

import com.ai.agent.backend.exception.ParameterConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import software.amazon.awssdk.services.bedrockagentruntime.model.Parameter;

/**
 * A resolver that processes the @OpenApiRequest annotation and converts AWS Bedrock Parameters
 * to the target DTO type using Spring's ConversionService.
 * 
 * Note: This is not actually used in Spring MVC context but in our custom invocation chain
 * for AWS Bedrock agent actions.
 */
@Component
public class OpenApiRequestResolver implements HandlerMethodArgumentResolver {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiRequestResolver.class);
    private final ConversionService conversionService;
    
    public OpenApiRequestResolver(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
    }    
    
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        // This method won't actually be called in our context, but it's required by the interface
        throw new UnsupportedOperationException("This resolver is not designed to be used in a web request context");
    }
    
    /**
     * Resolve the argument in our custom context for AWS Bedrock agent actions.
     * 
     * @param parameter the method parameter to resolve
     * @param parameters the list of AWS Bedrock Parameters
     * @return the resolved argument value
     */
    public Object resolveArgument(MethodParameter parameter, Parameter openApiParameter) {        
        Class<?> targetType = parameter.getParameterType();        
        
        try {            
            // Use Spring's ConversionService to convert from List<Parameter> to the target type
            if (conversionService.canConvert(String.class, targetType)) {
                return conversionService.convert(openApiParameter.value(), targetType);
            } else {
                throw new ParameterConversionException("No converter found from List<Parameter> to " + targetType.getName());
            }            
        } catch (Exception e) {            
            logger.warn("Failed to convert parameter to {}, returning null", targetType.getName(), e);
            throw new ParameterConversionException("Failed to convert parameter to " + targetType.getName(), e);                    
        }
    }

} 