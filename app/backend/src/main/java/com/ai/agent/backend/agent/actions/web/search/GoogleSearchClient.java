package com.ai.agent.backend.agent.actions.web.search;

import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.agent.backend.model.GoogleSearchResponse;
import com.ai.agent.backend.utility.GoogleSearchUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class GoogleSearchClient {
    private final RestClient googleSearchClient;
    private static final Logger logger = LoggerFactory.getLogger(GoogleSearchClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GoogleSearchClient(String googleSearchApiKey, String googleSearchEngineId) {
        String endpoint = String.format("https://www.googleapis.com/customsearch/v1?key=%s&cx=%s",
                googleSearchApiKey, googleSearchEngineId);

        logger.info("Google Search Endpoint: {}", endpoint);

        this.googleSearchClient = RestClient.builder()
        .baseUrl(endpoint)
        .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        .build();        
    }

    /**
     * Performs a Google search and returns the full response object
     * 
     * @param query The search query
     * @return The parsed GoogleSearchResponse object
     * @throws IOException If there's an error parsing the response
     */
    public GoogleSearchResponse search(String query) throws IOException {
        String responseJson = googleSearchClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("q", query).build())
                .retrieve()
                .body(String.class);
        
        // Parse the JSON response
        GoogleSearchResponse response = objectMapper.readValue(responseJson, GoogleSearchResponse.class);
        
        // Log some information about the response
        logger.info("Search returned {} results", response.getSearchInformation().getTotalResults());
        
        return response;
    }
    
    /**
     * Performs a Google search and returns a formatted summary of results
     * 
     * @param query The search query
     * @return A formatted string with search result summaries
     */
    public String searchFormatted(String query) {
        try {
            GoogleSearchResponse response = search(query);
            return GoogleSearchUtils.formatSearchResults(response);
        } catch (Exception e) {
            logger.error("Error performing formatted Google search", e);
            return "Error performing search: " + e.getMessage();
        }
    }
}
