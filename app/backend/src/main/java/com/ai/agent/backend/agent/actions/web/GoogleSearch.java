package com.ai.agent.backend.agent.actions.web;

import com.ai.agent.backend.agent.actions.web.search.GoogleSearchClient;
import com.ai.agent.backend.model.GoogleSearchResponse;
import software.amazon.awssdk.services.bedrockagentruntime.model.Parameter;

import com.ai.agent.backend.agent.actions.web.parser.JsoupParserClient;
import com.ai.agent.backend.agent.actions.AgentAction;
import com.ai.agent.backend.constant.enums.OperationId;
import java.util.List;
import java.io.IOException;
import com.ai.agent.backend.utility.GoogleSearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class GoogleSearch implements WebSearch<String, GoogleSearchResponse>, AgentAction<List<String>>{
   
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

    @Override
    public List<String> execute(OperationId operationId, List<Parameter> parameters) {
        String query = parameters.stream()
                                .map(Parameter::value)
                                .reduce("", (a, b) -> a + " " + b)
                                .trim();
        
        GoogleSearchResponse result = search(query);
        List<String> urls = GoogleSearchUtils.extractLinks(result);
        logger.info("Google Search Result: {}", urls);
        List<String> contents = extractContents(urls);
        logger.info("Contents: {}", contents.size());
        return contents;
    }

    private List<String> extractContents(List<String> urls) {
        return urls.stream()
                .map(url -> jsoupParserClient.extractMainContent(url))
                .collect(Collectors.toList());
    }
}
