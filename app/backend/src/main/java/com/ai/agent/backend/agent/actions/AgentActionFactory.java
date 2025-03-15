package com.ai.agent.backend.agent.actions;

import org.springframework.stereotype.Component;

import com.ai.agent.backend.constant.enums.ActionGroup;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.ai.agent.backend.agent.actions.web.GoogleSearch;
import com.ai.agent.backend.agent.actions.save.SaveClient;
import com.ai.agent.backend.agent.actions.image_generation.BedrockImageGenerator;

@Component
public class AgentActionFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    public AgentAction createAction(ActionGroup actionGroup) {
        switch (actionGroup) {
            case WEB_SEARCH:                
                return applicationContext.getBean(GoogleSearch.class);
            case SAVE:
                return applicationContext.getBean(SaveClient.class);
            case IMAGE_GENERATION:
                return applicationContext.getBean(BedrockImageGenerator.class);
            default:
                throw new IllegalArgumentException("Invalid action group: " + actionGroup);
        }
    }
}
