package com.ai.agent.backend.agent.events;


import com.ai.agent.backend.agent.service.AgentService;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SQSMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(SQSMessageListener.class);
    private final AgentService agentService;

    public SQSMessageListener(AgentService agentService) {
        this.agentService = agentService;
    }

    @SqsListener("${aws.sqs.queue.name}")
    public void receiveMessage(String message){
        logger.info("Received Message: {}", message);

        agentService.generateContent(message);
    }
}
