package com.sheruba.research_agent.dto;

public class SearchResult {
    private String title;
    private String url;
    private String snippet;
    private String domain;
    private String rawContent;

    public SearchResult() {
    }

    public SearchResult(String title, String url, String snippet, String domain) {
        this.title = title;
        this.url = url;
        this.snippet = snippet;
        this.domain = domain;
    }

    public SearchResult(String title, String url, String snippet, String domain, String rawContent) {
        this.title = title;
        this.url = url;
        this.snippet = snippet;
        this.domain = domain;
        this.rawContent = rawContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }
}
