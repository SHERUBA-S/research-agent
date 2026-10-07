package com.sheruba.research_agent.service;

import com.sheruba.research_agent.dto.AIReportResult;
import com.sheruba.research_agent.dto.ResearchResponse;
import com.sheruba.research_agent.dto.ResearchSummaryResponse;
import com.sheruba.research_agent.entity.Research;
import java.util.List;

public interface ReportService {

    /**
     * Serializes a list of strings to JSON.
     */
    String toJsonString(List<String> list);

    /**
     * Deserializes a JSON string into a list of strings.
     */
    List<String> fromJsonString(String json);

    /**
     * Applies AI report generation results to the research entity.
     */
    void applyReportResult(Research research, AIReportResult result);

    /**
     * Maps a Research entity into a full ResearchResponse DTO.
     */
    ResearchResponse mapToResponse(Research research);

    /**
     * Maps a Research entity into a summary response DTO for history list.
     */
    ResearchSummaryResponse mapToSummary(Research research);
}
