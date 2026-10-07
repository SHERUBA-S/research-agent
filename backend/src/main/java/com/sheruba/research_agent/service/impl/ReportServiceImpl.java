package com.sheruba.research_agent.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheruba.research_agent.dto.AIReportResult;
import com.sheruba.research_agent.dto.ResearchResponse;
import com.sheruba.research_agent.dto.ResearchSummaryResponse;
import com.sheruba.research_agent.dto.SourceDto;
import com.sheruba.research_agent.entity.Research;
import com.sheruba.research_agent.entity.ResearchSource;
import com.sheruba.research_agent.service.ReportService;
import com.sheruba.research_agent.service.SourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ObjectMapper objectMapper;
    private final SourceService sourceService;

    public ReportServiceImpl(ObjectMapper objectMapper, SourceService sourceService) {
        this.objectMapper = objectMapper;
        this.sourceService = sourceService;
    }

    @Override
    public String toJsonString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            log.warn("Error serializing string list to JSON: {}", e.getMessage());
            return "[]";
        }
    }

    @Override
    public List<String> fromJsonString(String json) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Error deserializing JSON list '{}': {}", json, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void applyReportResult(Research research, AIReportResult result) {
        if (result == null) {
            return;
        }

        if (result.getTitle() != null && !result.getTitle().isBlank()) {
            research.setTitle(result.getTitle().trim());
        } else if (research.getTitle() == null) {
            research.setTitle(research.getQuery());
        }

        research.setSummary(result.getSummary());
        research.setAnalysis(result.getAnalysis());
        research.setKeyFindingsJson(toJsonString(result.getKeyFindings()));
        research.setImportantInsightsJson(toJsonString(result.getImportantInsights()));
    }

    @Override
    public ResearchResponse mapToResponse(Research research) {
        if (research == null) {
            return null;
        }

        ResearchResponse response = new ResearchResponse();
        response.setId(research.getId());
        response.setQuery(research.getQuery());
        response.setStatus(research.getStatus());
        response.setTitle(research.getTitle() != null ? research.getTitle() : research.getQuery());
        response.setSummary(research.getSummary());
        response.setAnalysis(research.getAnalysis());
        response.setKeyFindings(fromJsonString(research.getKeyFindingsJson()));
        response.setImportantInsights(fromJsonString(research.getImportantInsightsJson()));
        response.setErrorMessage(research.getErrorMessage());
        response.setCreatedAt(research.getCreatedAt());
        response.setUpdatedAt(research.getUpdatedAt());

        List<SourceDto> sourceDtos = sourceService.mapToDtos(research.getSources());
        response.setSources(sourceDtos);

        return response;
    }

    @Override
    public ResearchSummaryResponse mapToSummary(Research research) {
        if (research == null) {
            return null;
        }

        int count = research.getSources() != null ? research.getSources().size() : 0;
        String title = research.getTitle() != null && !research.getTitle().isBlank()
                ? research.getTitle()
                : research.getQuery();

        return new ResearchSummaryResponse(
                research.getId(),
                research.getQuery(),
                title,
                research.getStatus(),
                count,
                research.getCreatedAt(),
                research.getUpdatedAt()
        );
    }
}
