package com.sheruba.research_agent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheruba.research_agent.dto.AIReportResult;
import com.sheruba.research_agent.dto.ResearchResponse;
import com.sheruba.research_agent.entity.Research;
import com.sheruba.research_agent.entity.ResearchStatus;
import com.sheruba.research_agent.service.impl.ReportServiceImpl;
import com.sheruba.research_agent.service.impl.SourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        SourceService sourceService = new SourceServiceImpl();
        reportService = new ReportServiceImpl(objectMapper, sourceService);
    }

    @Test
    void testJsonSerializationAndDeserialization() {
        List<String> findings = List.of("Finding A with citation [1]", "Finding B with citation [2]");
        String json = reportService.toJsonString(findings);

        assertNotNull(json);
        assertTrue(json.contains("Finding A"));

        List<String> deserialized = reportService.fromJsonString(json);
        assertEquals(2, deserialized.size());
        assertEquals("Finding A with citation [1]", deserialized.get(0));
        assertEquals("Finding B with citation [2]", deserialized.get(1));
    }

    @Test
    void testApplyReportResult() {
        Research research = new Research("r-1", "AI in Medicine");
        AIReportResult result = new AIReportResult(
                "Artificial Intelligence in Modern Medicine",
                "Summary paragraph here...",
                List.of("Key insight 1", "Key insight 2"),
                "Detailed comprehensive analysis text...",
                List.of("Strategic point 1")
        );

        reportService.applyReportResult(research, result);

        assertEquals("Artificial Intelligence in Modern Medicine", research.getTitle());
        assertEquals("Summary paragraph here...", research.getSummary());
        assertEquals("Detailed comprehensive analysis text...", research.getAnalysis());
        assertNotNull(research.getKeyFindingsJson());
    }

    @Test
    void testMapToResponse() {
        Research research = new Research("r-1", "AI in Medicine");
        research.setTitle("AI in Medicine Title");
        research.setStatus(ResearchStatus.COMPLETED);
        research.setSummary("Executive summary");
        research.setAnalysis("Detailed analysis");
        research.setKeyFindingsJson("[\"Finding 1\", \"Finding 2\"]");
        research.setImportantInsightsJson("[\"Insight 1\"]");

        ResearchResponse response = reportService.mapToResponse(research);

        assertNotNull(response);
        assertEquals("r-1", response.getId());
        assertEquals(ResearchStatus.COMPLETED, response.getStatus());
        assertEquals(2, response.getKeyFindings().size());
        assertEquals(1, response.getImportantInsights().size());
    }
}
