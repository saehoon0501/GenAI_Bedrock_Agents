package com.ai.agent.backend.agent.actions.web.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ai.agent.backend.utility.GoogleSearchUtils;

@Component
public class JsoupParserClient {
    private static final Logger logger = LoggerFactory.getLogger(JsoupParserClient.class);
    
    /**
     * Parses the content of a web page
     * 
     * @param url The URL to parse
     * @return The HTML content of the page, or empty string if an error occurs
     */
    public String parse(String url) {
        try {
            logger.info("Parsing URL: {}", url);
            return GoogleSearchUtils.createScrapingConnection(url)
                    .execute()
                    .body();
        } catch (IOException e) {
            logger.error("Error parsing URL: {}", url, e);
            return "";
        }
    }
    
    /**
     * Parses a web page and returns the Document object for further processing
     * 
     * @param url The URL to parse
     * @return The Jsoup Document object, or null if an error occurs
     */
    public Document parseDocument(String url) {
        try {
            logger.info("Parsing document from URL: {}", url);
            return GoogleSearchUtils.createScrapingConnection(url)
                    .execute()
                    .parse();
        } catch (IOException e) {
            logger.error("Error parsing document from URL: {}", url, e);
            return null;
        }
    }
    
    /**
     * Extracts text content from elements matching a CSS selector
     * 
     * @param url The URL to parse
     * @param cssSelector The CSS selector to match elements
     * @return List of text content from matching elements
     */
    public List<String> extractElements(String url, String cssSelector) {
        Document doc = parseDocument(url);
        if (doc == null) {
            return new ArrayList<>();
        }
        
        Elements elements = doc.select(cssSelector);
        return elements.stream()
                .map(Element::text)
                .collect(Collectors.toList());
    }
    
    /**
     * Extracts links (href attributes) from anchor elements matching a CSS selector
     * 
     * @param url The URL to parse
     * @param cssSelector The CSS selector to match elements (defaults to "a[href]" if empty)
     * @return List of URLs from matching elements
     */
    public List<String> extractLinks(String url, String cssSelector) {
        Document doc = parseDocument(url);
        if (doc == null) {
            return new ArrayList<>();
        }
        
        String selector = cssSelector.isEmpty() ? "a[href]" : cssSelector;
        Elements links = doc.select(selector);
        
        return links.stream()
                .map(link -> link.attr("abs:href"))
                .filter(href -> !href.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * Extracts the main content from a webpage (article text, main content div, etc.)
     * Uses common content selectors to find the most likely main content
     * 
     * @param url The URL to parse
     * @return The extracted main content text
     */
    public String extractMainContent(String url) {
        Document doc = parseDocument(url);
        if (doc == null) {
            return "";
        }
        
        // Try common content selectors in order of likelihood
        String[] contentSelectors = {
            "article", "main", ".content", "#content", ".post-content", 
            ".article-content", ".entry-content", ".post", ".article", "p"
        };
        
        for (String selector : contentSelectors) {
            Elements elements = doc.select(selector);
            if (!elements.isEmpty()) {
                return elements.text();
            }
        }
        
        // Fallback to body text if no content elements found
        return doc.body().text();
    }
}
