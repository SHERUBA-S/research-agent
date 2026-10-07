package com.sheruba.research_agent.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "research_sources")
public class ResearchSource {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "research_id", nullable = false)
    private Research research;

    @Column(name = "title", nullable = false, length = 1000)
    private String title;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @Column(name = "domain", length = 255)
    private String domain;

    @Column(name = "snippet", columnDefinition = "TEXT")
    private String snippet;

    @Column(name = "citation_number", nullable = false)
    private Integer citationNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public ResearchSource() {
    }

    public ResearchSource(String id, Research research, String title, String url, String domain, String snippet, Integer citationNumber) {
        this.id = id;
        this.research = research;
        this.title = title;
        this.url = url;
        this.domain = domain;
        this.snippet = snippet;
        this.citationNumber = citationNumber;
        this.createdAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Research getResearch() {
        return research;
    }

    public void setResearch(Research research) {
        this.research = research;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
