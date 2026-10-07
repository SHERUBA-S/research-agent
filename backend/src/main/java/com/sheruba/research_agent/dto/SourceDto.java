package com.sheruba.research_agent.dto;

public class SourceDto {
    private String id;
    private String title;
    private String url;
    private String domain;
    private String snippet;
    private Integer citationNumber;

    public SourceDto() {
    }

    public SourceDto(String id, String title, String url, String domain, String snippet, Integer citationNumber) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.domain = domain;
        this.snippet = snippet;
        this.citationNumber = citationNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Integer getCitationNumber() {
        return citationNumber;
    }

    public void setCitationNumber(Integer citationNumber) {
        this.citationNumber = citationNumber;
    }
}
