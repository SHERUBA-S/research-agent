package com.sheruba.research_agent.dto;

import com.sheruba.research_agent.entity.ResearchStatus;
import java.time.Instant;

public class ResearchSummaryResponse {
    private String id;
    private String query;
    private String title;
    private ResearchStatus status;
    private int sourcesCount;
    private Instant createdAt;
    private Instant updatedAt;

    public ResearchSummaryResponse() {
    }

    public ResearchSummaryResponse(String id, String query, String title, ResearchStatus status, int sourcesCount, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.query = query;
        this.title = title;
        this.status = status;
        this.sourcesCount = sourcesCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public ResearchStatus getStatus() {
        return status;
    }

    public void setStatus(ResearchStatus status) {
        this.status = status;
    }

    public int getSourcesCount() {
        return sourcesCount;
    }

    public void setSourcesCount(int sourcesCount) {
        this.sourcesCount = sourcesCount;
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
