package com.ai.agent.backend.exception;

/**
 * Exception thrown when there is an error converting AWS Bedrock Parameters to DTOs.
 */
public class ParameterConversionException extends RuntimeException {

    public ParameterConversionException(String message) {
        super(message);
    }

    public ParameterConversionException(String message, Throwable cause) {
        super(message, cause);
    }
} 