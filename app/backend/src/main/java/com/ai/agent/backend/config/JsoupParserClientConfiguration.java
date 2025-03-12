package com.ai.agent.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ai.agent.backend.agent.actions.web.parser.JsoupParserClient;

@Configuration
public class JsoupParserClientConfiguration {
    @Bean
    public JsoupParserClient jsoupParserClient() {
        return new JsoupParserClient();
    }
}
