package com.sheruba.research_agent.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "researches")
public class Research {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "query", nullable = false, length = 2000)
    private String query;

    @Column(name = "title", length = 1000)
    private String title;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "analysis", columnDefinition = "TEXT")
    private String analysis;

    @Column(name = "key_findings", columnDefinition = "TEXT")
    private String keyFindingsJson;

    @Column(name = "important_insights", columnDefinition = "TEXT")
    private String importantInsightsJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ResearchStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @OneToMany(mappedBy = "research", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("citationNumber ASC")
    private List<ResearchSource> sources = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Research() {
    }

    public Research(String id, String query) {
        this.id = id;
        this.query = query;
        this.status = ResearchStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void addSource(ResearchSource source) {
        sources.add(source);
        source.setResearch(this);
    }

    public void removeSource(ResearchSource source) {
        sources.remove(source);
        source.setResearch(null);
    }

    public void clearSources() {
        for (ResearchSource source : new ArrayList<>(this.sources)) {
            removeSource(source);
        }
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getKeyFindingsJson() {
        return keyFindingsJson;
    }

    public void setKeyFindingsJson(String keyFindingsJson) {
        this.keyFindingsJson = keyFindingsJson;
    }

    public String getImportantInsightsJson() {
        return importantInsightsJson;
    }

    public void setImportantInsightsJson(String importantInsightsJson) {
        this.importantInsightsJson = importantInsightsJson;
    }

    public ResearchStatus getStatus() {
        return status;
    }

    public void setStatus(ResearchStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<ResearchSource> getSources() {
        return sources;
    }

    public void setSources(List<ResearchSource> sources) {
        this.sources = sources;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
