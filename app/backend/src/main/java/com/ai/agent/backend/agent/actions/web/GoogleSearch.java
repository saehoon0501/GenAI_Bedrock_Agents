package com.ai.agent.backend.agent.actions.web;

import com.ai.agent.backend.agent.actions.web.search.GoogleSearchClient;
import com.ai.agent.backend.model.GoogleSearchResponse;
import com.ai.agent.backend.model.AgentResponse;
import com.ai.agent.backend.agent.actions.web.parser.JsoupParserClient;
import com.ai.agent.backend.agent.actions.AgentAction;

import java.util.List;
import java.io.IOException;
import com.ai.agent.backend.utility.GoogleSearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class GoogleSearch extends AgentAction implements WebSearch<String, GoogleSearchResponse> {
   
    private static final Logger logger = LoggerFactory.getLogger(GoogleSearch.class);
    private final GoogleSearchClient googleSearchClient;
    private final JsoupParserClient jsoupParserClient;

    public GoogleSearch(GoogleSearchClient googleSearchClient, JsoupParserClient jsoupParserClient) {
        this.googleSearchClient = googleSearchClient;
        this.jsoupParserClient = jsoupParserClient;
    }

    @Override
    public GoogleSearchResponse search(String query) {
        try {
            return googleSearchClient.search(query);
        } catch (IOException e) {
            logger.error("Error performing Google search", e);
            return null;
        }
    }
    
    /**
     * Handle search web content requests using the @OpenApiRequest annotation.
     * The framework will automatically convert the Parameters to SearchWebContentRequest.
     * 
     * @param request the automatically converted request object
     * @param parameters the original parameters (for reference if needed)
     * @return the agent response
     */
    public AgentResponse handleSearchWebContent(String query) {
        
        logger.info("Handling search web content request with query: {}", query);
        
        // Execute the search using the query from the converted request
        GoogleSearchResponse result = search(query);
        
        // Process the result
        if (result == null) {
            return AgentResponse.stringifyJson(List.of("No search results found"));
        }
        
        List<String> urls = GoogleSearchUtils.extractLinks(result);
        logger.info("Google Search Result: {}", urls);
        
        List<String> contents = extractContents(urls);
        logger.info("Contents: {}", contents.size());
        return AgentResponse.stringifyJson(contents);
    }

    private List<String> extractContents(List<String> urls) {
        return urls.stream()
                .map(url -> jsoupParserClient.extractMainContent(url))
                .collect(Collectors.toList());
    }
}
