package com.ai.agent.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.agent.backend.agent.service.AgentService;

@RestController
public class AgentController {
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);
    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/api/agent")
    public void searchAndParseWebContent(@RequestBody String query) {
        logger.info("Searching and parsing web content for query: {}", query);
        agentService.invokeAgent(query);        
    }
    
}
