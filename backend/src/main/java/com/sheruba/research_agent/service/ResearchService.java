package com.sheruba.research_agent.service;

import com.sheruba.research_agent.dto.ResearchRequest;
import com.sheruba.research_agent.dto.ResearchResponse;
import com.sheruba.research_agent.dto.ResearchSummaryResponse;
import java.util.List;

public interface ResearchService {

    /**
     * Starts and executes a complete research pipeline for the given request.
     */
    ResearchResponse conductResearch(ResearchRequest request);

    /**
     * Retrieves full research report by research ID.
     */
    ResearchResponse getResearchById(String id);

    /**
     * Lists all previous research summaries ordered newest first.
     */
    List<ResearchSummaryResponse> getAllResearches();

    /**
     * Deletes a research record and its associated sources.
     */
    void deleteResearch(String id);
}
