package com.ai.agent.backend.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleSearchResponse {
    private String kind;
    private UrlInfo url;
    private Map<String, List<QueryInfo>> queries;
    private Context context;
    private SearchInformation searchInformation;
    private List<SearchItem> items;

    // Getters and setters
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public UrlInfo getUrl() {
        return url;
    }

    public void setUrl(UrlInfo url) {
        this.url = url;
    }

    public Map<String, List<QueryInfo>> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, List<QueryInfo>> queries) {
        this.queries = queries;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public SearchInformation getSearchInformation() {
        return searchInformation;
    }

    public void setSearchInformation(SearchInformation searchInformation) {
        this.searchInformation = searchInformation;
    }

    public List<SearchItem> getItems() {
        return items;
    }

    public void setItems(List<SearchItem> items) {
        this.items = items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UrlInfo {
        private String type;
        private String template;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
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

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(String totalResults) {
            this.totalResults = totalResults;
        }

        public String getSearchTerms() {
            return searchTerms;
        }

        public void setSearchTerms(String searchTerms) {
            this.searchTerms = searchTerms;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public String getInputEncoding() {
            return inputEncoding;
        }

        public void setInputEncoding(String inputEncoding) {
            this.inputEncoding = inputEncoding;
        }

        public String getOutputEncoding() {
            return outputEncoding;
        }

        public void setOutputEncoding(String outputEncoding) {
            this.outputEncoding = outputEncoding;
        }

        public String getSafe() {
            return safe;
        }

        public void setSafe(String safe) {
            this.safe = safe;
        }

        public String getCx() {
            return cx;
        }

        public void setCx(String cx) {
            this.cx = cx;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Context {
        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public void setSearchTime(double searchTime) {
            this.searchTime = searchTime;
        }

        public String getFormattedSearchTime() {
            return formattedSearchTime;
        }

        public void setFormattedSearchTime(String formattedSearchTime) {
            this.formattedSearchTime = formattedSearchTime;
        }

        public String getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(String totalResults) {
            this.totalResults = totalResults;
        }

        public String getFormattedTotalResults() {
            return formattedTotalResults;
        }

        public void setFormattedTotalResults(String formattedTotalResults) {
            this.formattedTotalResults = formattedTotalResults;
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

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getHtmlTitle() {
            return htmlTitle;
        }

        public void setHtmlTitle(String htmlTitle) {
            this.htmlTitle = htmlTitle;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getDisplayLink() {
            return displayLink;
        }

        public void setDisplayLink(String displayLink) {
            this.displayLink = displayLink;
        }

        public String getSnippet() {
            return snippet;
        }

        public void setSnippet(String snippet) {
            this.snippet = snippet;
        }

        public String getHtmlSnippet() {
            return htmlSnippet;
        }

        public void setHtmlSnippet(String htmlSnippet) {
            this.htmlSnippet = htmlSnippet;
        }

        public String getFormattedUrl() {
            return formattedUrl;
        }

        public void setFormattedUrl(String formattedUrl) {
            this.formattedUrl = formattedUrl;
        }

        public String getHtmlFormattedUrl() {
            return htmlFormattedUrl;
        }

        public void setHtmlFormattedUrl(String htmlFormattedUrl) {
            this.htmlFormattedUrl = htmlFormattedUrl;
        }

        public Map<String, Object> getPageMap() {
            return pageMap;
        }

        public void setPageMap(Map<String, Object> pageMap) {
            this.pageMap = pageMap;
        }
    }
} 