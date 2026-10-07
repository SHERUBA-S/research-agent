package com.sheruba.research_agent.service;

import com.sheruba.research_agent.dto.AIReportResult;
import com.sheruba.research_agent.dto.SearchResult;
import java.util.List;

public interface AIService {

    /**
     * Synthesizes and analyzes the research query and collected web sources.
     *
     * @param query The user's research query.
     * @param sources The verified search results and content extracts.
     * @return Structured AIReportResult with title, summary, key findings, detailed analysis, and important insights.
     */
    AIReportResult generateResearchReport(String query, List<SearchResult> sources);

    /**
     * Returns the name of the AI provider (e.g. "gemini", "openai").
     */
    String getProviderName();

    /**
     * Checks if this AI provider has valid credentials and is ready for calls.
     */
    boolean isAvailable();
}
