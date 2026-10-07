package com.sheruba.research_agent.dto;

import com.sheruba.research_agent.entity.ResearchStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ResearchResponse {
    private String id;
    private String query;
    private ResearchStatus status;
    private String title;
    private String summary;
    private List<String> keyFindings = new ArrayList<>();
    private String analysis;
    private List<String> importantInsights = new ArrayList<>();
    private List<SourceDto> sources = new ArrayList<>();
    private String errorMessage;
    private Instant createdAt;
    private Instant updatedAt;

    public ResearchResponse() {
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

    public ResearchStatus getStatus() {
        return status;
    }

    public void setStatus(ResearchStatus status) {
        this.status = status;
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

    public List<String> getKeyFindings() {
        return keyFindings;
    }

    public void setKeyFindings(List<String> keyFindings) {
        this.keyFindings = keyFindings != null ? keyFindings : new ArrayList<>();
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public List<String> getImportantInsights() {
        return importantInsights;
    }

    public void setImportantInsights(List<String> importantInsights) {
        this.importantInsights = importantInsights != null ? importantInsights : new ArrayList<>();
    }

    public List<SourceDto> getSources() {
        return sources;
    }

    public void setSources(List<SourceDto> sources) {
        this.sources = sources != null ? sources : new ArrayList<>();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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
