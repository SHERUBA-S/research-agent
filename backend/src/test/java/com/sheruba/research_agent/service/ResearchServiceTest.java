package com.sheruba.research_agent.service;

import com.sheruba.research_agent.dto.AIReportResult;
import com.sheruba.research_agent.dto.ResearchRequest;
import com.sheruba.research_agent.dto.ResearchResponse;
import com.sheruba.research_agent.dto.SearchResult;
import com.sheruba.research_agent.entity.Research;
import com.sheruba.research_agent.entity.ResearchStatus;
import com.sheruba.research_agent.exception.ResearchException;
import com.sheruba.research_agent.exception.SearchException;
import com.sheruba.research_agent.repository.ResearchRepository;
import com.sheruba.research_agent.service.impl.ResearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResearchServiceTest {

    @Mock
    private ResearchRepository researchRepository;
    @Mock
    private SearchService searchService;
    @Mock
    private ContentExtractionService contentExtractionService;
    @Mock
    private AIService aiService;
    @Mock
    private SourceService sourceService;
    @Mock
    private ReportService reportService;

    private ResearchService researchService;

    @BeforeEach
    void setUp() {
        researchService = new ResearchServiceImpl(
                researchRepository,
                searchService,
                contentExtractionService,
                aiService,
                sourceService,
                reportService,
                5
        );
    }

    @Test
    void testConductResearch_Success() {
        ResearchRequest request = new ResearchRequest("Quantum Computing Advances");
        List<SearchResult> searchResults = List.of(
                new SearchResult("Quantum Breakthrough", "https://example.com/quantum", "Snippet...", "example.com")
        );
        AIReportResult aiResult = new AIReportResult(
                "Advances in Quantum Computing",
                "Summary...",
                List.of("Finding 1 [1]"),
                "Analysis...",
                List.of("Insight 1")
        );
        ResearchResponse mockResponse = new ResearchResponse();
        mockResponse.setId("res-123");
        mockResponse.setStatus(ResearchStatus.COMPLETED);
        mockResponse.setTitle("Advances in Quantum Computing");

        when(researchRepository.save(any(Research.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(searchService.search(anyString(), anyInt())).thenReturn(searchResults);
        when(sourceService.deduplicateAndNormalize(anyList())).thenReturn(searchResults);
        when(contentExtractionService.enrichWithContent(anyList(), anyInt())).thenReturn(searchResults);
        when(aiService.generateResearchReport(anyString(), anyList())).thenReturn(aiResult);
        when(sourceService.createSources(any(Research.class), anyList())).thenReturn(new ArrayList<>());
        when(reportService.mapToResponse(any(Research.class))).thenReturn(mockResponse);

        ResearchResponse response = researchService.conductResearch(request);

        assertNotNull(response);
        assertEquals("res-123", response.getId());
        assertEquals(ResearchStatus.COMPLETED, response.getStatus());
        verify(searchService).search("Quantum Computing Advances", 5);
        verify(aiService).generateResearchReport("Quantum Computing Advances", searchResults);
    }

    @Test
    void testConductResearch_SearchFailure_ThrowsResearchException() {
        ResearchRequest request = new ResearchRequest("Quantum Computing Advances");

        when(researchRepository.save(any(Research.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(searchService.search(anyString(), anyInt())).thenThrow(new SearchException("Search API timeout"));

        assertThrows(ResearchException.class, () -> researchService.conductResearch(request));
        verify(researchRepository, atLeastOnce()).save(argThat(r -> r.getStatus() == ResearchStatus.FAILED));
    }

    @Test
    void testGetResearchById_Success() {
        Research research = new Research("res-123", "Test Query");
        research.setStatus(ResearchStatus.COMPLETED);
        ResearchResponse mockResponse = new ResearchResponse();
        mockResponse.setId("res-123");

        when(researchRepository.findByIdWithSources("res-123")).thenReturn(Optional.of(research));
        when(reportService.mapToResponse(research)).thenReturn(mockResponse);

        ResearchResponse response = researchService.getResearchById("res-123");
        assertNotNull(response);
        assertEquals("res-123", response.getId());
    }
}
