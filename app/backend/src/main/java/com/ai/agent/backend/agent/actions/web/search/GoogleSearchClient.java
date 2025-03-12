package com.ai.agent.backend.agent.actions.web.search;

import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleSearchClient {
    private final RestClient googleSearchClient;
    private static final Logger logger = LoggerFactory.getLogger(GoogleSearchClient.class);

    public GoogleSearchClient(String googleSearchApiKey, String googleSearchEngineId) {
        String endpoint = "https://www.googleapis.com/customsearch/v1?key=%s&cx=%s"
        .formatted(googleSearchApiKey, googleSearchEngineId);

        logger.info("Google Search Endpoint: {}", endpoint);

        this.googleSearchClient = RestClient.builder()
        .baseUrl(endpoint)
        .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        .build();
    }

    public String search(String query) {
        logger.info("Google Search Query: {}", query);
        return googleSearchClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("q", query).build())
                .retrieve()
                .body(String.class);
    }
}
