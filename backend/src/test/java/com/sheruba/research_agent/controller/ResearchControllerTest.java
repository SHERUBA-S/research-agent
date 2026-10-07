package com.sheruba.research_agent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheruba.research_agent.dto.ResearchRequest;
import com.sheruba.research_agent.dto.ResearchResponse;
import com.sheruba.research_agent.dto.ResearchSummaryResponse;
import com.sheruba.research_agent.entity.ResearchStatus;
import com.sheruba.research_agent.exception.ResourceNotFoundException;
import com.sheruba.research_agent.service.ResearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResearchController.class)
class ResearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResearchService researchService;

    @Test
    void testCreateResearch_ValidRequest_ReturnsOk() throws Exception {
        ResearchRequest request = new ResearchRequest("Applications of AI in healthcare");
        ResearchResponse response = new ResearchResponse();
        response.setId("res-uuid-1");
        response.setQuery(request.getQuery());
        response.setTitle("AI in Healthcare Research");
        response.setStatus(ResearchStatus.COMPLETED);
        response.setSummary("Executive summary of AI applications");
        response.setCreatedAt(Instant.now());

        when(researchService.conductResearch(any(ResearchRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/research")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("res-uuid-1"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.title").value("AI in Healthcare Research"));
    }

    @Test
    void testCreateResearch_BlankQuery_ReturnsBadRequest() throws Exception {
        ResearchRequest request = new ResearchRequest("  ");

        mockMvc.perform(post("/api/research")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void testGetResearchById_Found() throws Exception {
        ResearchResponse response = new ResearchResponse();
        response.setId("res-uuid-1");
        response.setTitle("AI Research");
        response.setStatus(ResearchStatus.COMPLETED);

        when(researchService.getResearchById("res-uuid-1")).thenReturn(response);

        mockMvc.perform(get("/api/research/res-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("res-uuid-1"))
                .andExpect(jsonPath("$.title").value("AI Research"));
    }

    @Test
    void testGetResearchById_NotFound() throws Exception {
        when(researchService.getResearchById("unknown-id"))
                .thenThrow(new ResourceNotFoundException("Research task not found with ID: unknown-id"));

        mockMvc.perform(get("/api/research/unknown-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testGetAllResearches() throws Exception {
        ResearchSummaryResponse item = new ResearchSummaryResponse(
                "res-1", "AI Query", "AI Title", ResearchStatus.COMPLETED, 5, Instant.now(), Instant.now()
        );
        when(researchService.getAllResearches()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/research"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("res-1"))
                .andExpect(jsonPath("$[0].title").value("AI Title"));
    }

    @Test
    void testDeleteResearch() throws Exception {
        doNothing().when(researchService).deleteResearch("res-1");

        mockMvc.perform(delete("/api/research/res-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
