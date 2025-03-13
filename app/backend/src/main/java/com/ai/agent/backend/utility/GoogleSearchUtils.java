package com.ai.agent.backend.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.ai.agent.backend.model.GoogleSearchResponse;
import com.ai.agent.backend.model.GoogleSearchResponse.SearchItem;

/**
 * Utility class for processing Google Search results
 */
public class GoogleSearchUtils {
    private static final Logger logger = LoggerFactory.getLogger(GoogleSearchUtils.class);
    private static final int TIMEOUT_MS = 10000; // 10 seconds
    
    /**
     * Creates a configured Jsoup connection for web scraping
     * 
     * @param url The URL to connect to
     * @return A configured Jsoup Connection object
     */
    public static Connection createScrapingConnection(String url) {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .timeout(TIMEOUT_MS)
                .followRedirects(true)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .maxBodySize(0) // Unlimited
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .referrer("https://www.google.com");
    }
    
    /**
     * Extracts a list of URLs from a comma-separated string, filtering for URLs with path depth > 0
     * 
     * @param urlsString Comma-separated list of URLs
     * @return List of URL strings with at least one path segment
     */
    public static List<String> extractUrlsWithPath(String urlsString) {
        if (urlsString == null || urlsString.isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(urlsString.split(","))
            .map(String::trim)
            .filter(url -> {
                try {
                    URI uri = new URI(url);
                    String path = uri.getPath();
                    // Check if path has content beyond just "/"
                    return path != null && path.length() > 1;
                } catch (URISyntaxException e) {
                    return false;
                }
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Extracts only the links/URLs from search results
     * 
     * @param response The Google search response
     * @return A list of URLs from the search results
     */
    public static List<String> extractLinks(GoogleSearchResponse response) {
        if (response == null || response.getItems() == null) {
            return new ArrayList<>();
        }
        
        return response.getItems().stream()
                .map(SearchItem::getLink)
                .collect(Collectors.toList());
    }
    
    /**
     * Formats search results into a readable summary
     * 
     * @param response The Google search response
     * @return A formatted summary of the search results
     */
    public static String formatSearchResults(GoogleSearchResponse response) {
        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            return "No search results found.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Found %s results for '%s':\n\n", 
                response.getSearchInformation().getFormattedTotalResults(),
                response.getQueries().get("request").get(0).getSearchTerms()));
        
        for (int i = 0; i < response.getItems().size(); i++) {
            GoogleSearchResponse.SearchItem item = response.getItems().get(i);
            sb.append(String.format("%d. %s\n", i + 1, item.getTitle()));
            sb.append(String.format("   URL: %s\n", item.getLink()));
            sb.append(String.format("   %s\n\n", item.getSnippet()));
        }
        
        return sb.toString();
    }
    
    /**
     * Extracts just the titles and URLs from search results
     * 
     * @param response The Google search response
     * @return A list of "title: URL" strings
     */
    public static List<String> extractTitlesAndUrls(GoogleSearchResponse response) {
        if (response == null || response.getItems() == null) {
            return new ArrayList<>();
        }
        
        return response.getItems().stream()
                .map(item -> String.format("%s: %s", item.getTitle(), item.getLink()))
                .collect(Collectors.toList());
    }
} 