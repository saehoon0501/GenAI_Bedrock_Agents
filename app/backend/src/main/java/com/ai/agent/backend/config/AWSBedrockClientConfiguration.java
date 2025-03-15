package com.ai.agent.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;

import java.time.Duration;

@Configuration
public class AWSBedrockClientConfiguration {

    @Value("${aws.region}")
    String bedrockRegion;

    private final String BEDROCK_STABLE_DIFFUSION_REGION = "us-east-1";
    
    final int CLIENT_TIMEOUT = 600;

    @Bean
    public BedrockAgentRuntimeAsyncClient bedrockAgentRuntimeAsyncClient(AwsCredentialsProvider credentialsProvider){
        // Create a custom HTTP client with increased timeouts
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(CLIENT_TIMEOUT))
                .readTimeout(Duration.ofSeconds(CLIENT_TIMEOUT))
                .writeTimeout(Duration.ofSeconds(CLIENT_TIMEOUT))
                .connectionMaxIdleTime(Duration.ofSeconds(CLIENT_TIMEOUT))
                .maxConcurrency(100)
                .build();        
                
        ClientOverrideConfiguration clientConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofSeconds(CLIENT_TIMEOUT))
                .apiCallAttemptTimeout(Duration.ofSeconds(CLIENT_TIMEOUT))        
                .build();
                
        return BedrockAgentRuntimeAsyncClient.builder()
                .region(Region.of(bedrockRegion))
                .credentialsProvider(credentialsProvider)
                .httpClient(httpClient)
                .overrideConfiguration(clientConfig)
                .build();
    }

    @Bean
    public BedrockRuntimeClient bedrockRuntimeClient(AwsCredentialsProvider credentialsProvider) {
        ClientOverrideConfiguration clientConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofSeconds(CLIENT_TIMEOUT))
                .apiCallAttemptTimeout(Duration.ofSeconds(CLIENT_TIMEOUT))        
                .build();
                
        return BedrockRuntimeClient.builder()
                .region(Region.of(BEDROCK_STABLE_DIFFUSION_REGION))
                .credentialsProvider(credentialsProvider)
                .overrideConfiguration(clientConfig)
                .build();
    }
}
