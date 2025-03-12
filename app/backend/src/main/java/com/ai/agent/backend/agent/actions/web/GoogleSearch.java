package com.ai.agent.backend.agent.actions.web;

import com.ai.agent.backend.agent.actions.web.search.GoogleSearchClient;

import software.amazon.awssdk.services.bedrockagentruntime.model.Parameter;

import com.ai.agent.backend.agent.actions.web.parser.JsoupParserClient;
import com.ai.agent.backend.agent.actions.AgentAction;
import com.ai.agent.backend.constant.enums.OperationId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class GoogleSearch implements WebSearch<String, String>, AgentAction{
   
    private static final Logger logger = LoggerFactory.getLogger(GoogleSearch.class);
    private final GoogleSearchClient googleSearchClient;
    private final JsoupParserClient jsoupParserClient;

    public GoogleSearch(GoogleSearchClient googleSearchClient, JsoupParserClient jsoupParserClient) {
        this.googleSearchClient = googleSearchClient;
        this.jsoupParserClient = jsoupParserClient;
    }

    @Override
    public String search(String query) {
        return googleSearchClient.search(query);
    }

    @Override
    public void execute(OperationId operationId, List<Parameter> parameters) {
        String query = parameters.stream()
                                .map(Parameter::value)
                                .reduce("", (a, b) -> a + " " + b)
                                .trim();
        
        String result = search(query);
        logger.info("Google Search Result: {}", result);
    }
}
