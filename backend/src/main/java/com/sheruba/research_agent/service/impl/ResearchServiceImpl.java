package com.sheruba.research_agent.service.impl;

import com.sheruba.research_agent.dto.*;
import com.sheruba.research_agent.entity.Research;
import com.sheruba.research_agent.entity.ResearchSource;
import com.sheruba.research_agent.entity.ResearchStatus;
import com.sheruba.research_agent.exception.ResearchException;
import com.sheruba.research_agent.exception.ResourceNotFoundException;
import com.sheruba.research_agent.repository.ResearchRepository;
import com.sheruba.research_agent.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResearchServiceImpl implements ResearchService {

    private static final Logger log = LoggerFactory.getLogger(ResearchServiceImpl.class);

    private final ResearchRepository researchRepository;
    private final SearchService searchService;
    private final ContentExtractionService contentExtractionService;
    private final AIService aiService;
    private final SourceService sourceService;
    private final ReportService reportService;
    private final int searchMaxResults;

    public ResearchServiceImpl(
            ResearchRepository researchRepository,
            SearchService searchService,
            ContentExtractionService contentExtractionService,
            AIService aiService,
            SourceService sourceService,
            ReportService reportService,
            @Value("${app.search.max-results:6}") int searchMaxResults) {
        this.researchRepository = researchRepository;
        this.searchService = searchService;
        this.contentExtractionService = contentExtractionService;
        this.aiService = aiService;
        this.sourceService = sourceService;
        this.reportService = reportService;
        this.searchMaxResults = searchMaxResults;
    }

    @Override
    @Transactional
    public ResearchResponse conductResearch(ResearchRequest request) {
        String query = request.getQuery().trim();
        log.info("Starting new research task for query: '{}'", query);

        // 1. Initialize and persist Research record
        String researchId = UUID.randomUUID().toString();
        Research research = new Research(researchId, query);
        research.setStatus(ResearchStatus.IN_PROGRESS);
        research = researchRepository.save(research);

        try {
            // 2. Perform Web Search
            log.debug("[{}] Query processing & initiating web search...", researchId);
            List<SearchResult> rawResults = searchService.search(query, searchMaxResults);
            log.info("[{}] Search completed with {} raw results", researchId, rawResults.size());

            // 3. Deduplicate and normalize sources
            List<SearchResult> normalizedResults = sourceService.deduplicateAndNormalize(rawResults);

            // 4. Enrich top sources with deep content extraction
            log.debug("[{}] Enriching {} sources with content extraction...", researchId, normalizedResults.size());
            List<SearchResult> enrichedResults = contentExtractionService.enrichWithContent(normalizedResults, 4);

            // 5. AI Synthesis & Analysis
            log.info("[{}] Synthesizing report with AI provider...", researchId);
            AIReportResult aiResult = aiService.generateResearchReport(query, enrichedResults);
            log.info("[{}] AI report synthesis completed successfully", researchId);

            // 6. Build structured entity with sources and report details
            reportService.applyReportResult(research, aiResult);

            List<ResearchSource> persistentSources = sourceService.createSources(research, enrichedResults);
            research.clearSources();
            for (ResearchSource src : persistentSources) {
                research.addSource(src);
            }

            research.setStatus(ResearchStatus.COMPLETED);
            research.setErrorMessage(null);
            research = researchRepository.save(research);

            log.info("[{}] Research task successfully completed and saved with {} sources", researchId, persistentSources.size());
            return reportService.mapToResponse(research);

        } catch (Exception e) {
            log.error("[{}] Research task failed: {}", researchId, e.getMessage(), e);
            research.setStatus(ResearchStatus.FAILED);
            research.setErrorMessage(e.getMessage());
            researchRepository.save(research);

            throw new ResearchException("Failed to complete research: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResearchResponse getResearchById(String id) {
        Research research = researchRepository.findByIdWithSources(id)
                .orElseThrow(() -> new ResourceNotFoundException("Research task not found with ID: " + id));
        return reportService.mapToResponse(research);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResearchSummaryResponse> getAllResearches() {
        return researchRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(reportService::mapToSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteResearch(String id) {
        if (!researchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Research task not found with ID: " + id);
        }
        researchRepository.deleteById(id);
        log.info("Deleted research task: {}", id);
    }
}
