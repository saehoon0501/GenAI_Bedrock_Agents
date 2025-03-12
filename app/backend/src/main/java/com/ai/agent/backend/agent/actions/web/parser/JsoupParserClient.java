package com.ai.agent.backend.agent.actions.web.parser;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
@Component
public class JsoupParserClient {
    public List<String> parse(List<String> urls) {
        return urls.stream()
        .map(url -> {
            try {
                return Jsoup.connect(url).userAgent("Mozilla").execute().body();
            } catch (IOException e) {
                return "";
            }
        })
        .collect(Collectors.toList());
    }
}
