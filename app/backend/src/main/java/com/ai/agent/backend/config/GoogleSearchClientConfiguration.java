package com.ai.agent.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ai.agent.backend.agent.actions.web.search.GoogleSearchClient;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class GoogleSearchClientConfiguration {
    @Value("${google.search.api.key}")
    private String googleSearchApiKey;

    @Value("${google.search.engine.id}")
    private String googleSearchEngineId;

    @Bean
    public GoogleSearchClient googleSearchClient() {
        return new GoogleSearchClient(googleSearchApiKey, googleSearchEngineId);
    }
}
