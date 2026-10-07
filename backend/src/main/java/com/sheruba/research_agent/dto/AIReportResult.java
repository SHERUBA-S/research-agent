package com.sheruba.research_agent.dto;

import java.util.ArrayList;
import java.util.List;

public class AIReportResult {
    private String title;
    private String summary;
    private List<String> keyFindings = new ArrayList<>();
    private String analysis;
    private List<String> importantInsights = new ArrayList<>();

    public AIReportResult() {
    }

    public AIReportResult(String title, String summary, List<String> keyFindings, String analysis, List<String> importantInsights) {
        this.title = title;
        this.summary = summary;
        this.keyFindings = keyFindings != null ? keyFindings : new ArrayList<>();
        this.analysis = analysis;
        this.importantInsights = importantInsights != null ? importantInsights : new ArrayList<>();
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
}
