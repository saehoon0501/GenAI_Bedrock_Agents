package com.ai.agent.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;

@Configuration
public class AWSClientConfiguration {

    @Value("${aws.region}")
    String bedrockAgentRegion;

    @Bean
    public BedrockAgentRuntimeAsyncClient bedrockAgentRuntimeAsyncClient(AwsCredentialsProvider credentialsProvider){
        return BedrockAgentRuntimeAsyncClient.builder()
                .region(Region.of(bedrockAgentRegion))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
