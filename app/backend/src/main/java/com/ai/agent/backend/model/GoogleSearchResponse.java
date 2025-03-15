package com.ai.agent.backend.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class GoogleSearchResponse {
    private String kind;
    private UrlInfo url;
    private Map<String, List<QueryInfo>> queries;
    private Context context;
    private SearchInformation searchInformation;
    private List<SearchItem> items;

    // Getters
    public String getKind() {
        return kind;
    }

    public UrlInfo getUrl() {
        return url;
    }

    public Map<String, List<QueryInfo>> getQueries() {
        return queries;
    }

    public Context getContext() {
        return context;
    }

    public SearchInformation getSearchInformation() {
        return searchInformation;
    }

    public List<SearchItem> getItems() {
        return items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UrlInfo {
        private String type;
        private String template;

        public String getType() {
            return type;
        }

        public String getTemplate() {
            return template;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QueryInfo {
        private String title;
        private String totalResults;
        private String searchTerms;
        private int count;
        private int startIndex;
        private String inputEncoding;
        private String outputEncoding;
        private String safe;
        private String cx;

        public String getTitle() {
            return title;
        }

        public String getTotalResults() {
            return totalResults;
        }

        public String getSearchTerms() {
            return searchTerms;
        }

        public int getCount() {
            return count;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public String getInputEncoding() {
            return inputEncoding;
        }

        public String getOutputEncoding() {
            return outputEncoding;
        }

        public String getSafe() {
            return safe;
        }

        public String getCx() {
            return cx;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Context {
        private String title;

        public String getTitle() {
            return title;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchInformation {
        private double searchTime;
        private String formattedSearchTime;
        private String totalResults;
        private String formattedTotalResults;

        public double getSearchTime() {
            return searchTime;
        }

        public String getFormattedSearchTime() {
            return formattedSearchTime;
        }

        public String getTotalResults() {
            return totalResults;
        }

        public String getFormattedTotalResults() {
            return formattedTotalResults;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchItem {
        private String kind;
        private String title;
        private String htmlTitle;
        private String link;
        private String displayLink;
        private String snippet;
        private String htmlSnippet;
        private String formattedUrl;
        private String htmlFormattedUrl;
        @JsonProperty("pagemap")
        private Map<String, Object> pageMap;

        public String getKind() {
            return kind;
        }

        public String getTitle() {
            return title;
        }

        public String getHtmlTitle() {
            return htmlTitle;
        }

        public String getLink() {
            return link;
        }

        public String getDisplayLink() {
            return displayLink;
        }

        public String getSnippet() {
            return snippet;
        }

        public String getHtmlSnippet() {
            return htmlSnippet;
        }

        public String getFormattedUrl() {
            return formattedUrl;
        }

        public String getHtmlFormattedUrl() {
            return htmlFormattedUrl;
        }

        public Map<String, Object> getPageMap() {
            return pageMap;
        }
    }
} 