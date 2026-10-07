package com.sheruba.research_agent.controller;

import com.sheruba.research_agent.dto.ErrorResponse;
import com.sheruba.research_agent.dto.ResearchRequest;
import com.sheruba.research_agent.dto.ResearchResponse;
import com.sheruba.research_agent.dto.ResearchSummaryResponse;
import com.sheruba.research_agent.service.ResearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Research", description = "AI Research Agent Operations and Report Management")
public class ResearchController {

    private static final Logger log = LoggerFactory.getLogger(ResearchController.class);

    private final ResearchService researchService;

    public ResearchController(ResearchService researchService) {
        this.researchService = researchService;
    }

    @Operation(summary = "Conduct a new AI research task", description = "Searches the web, extracts content, and synthesizes a structured research report with citations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Research completed successfully",
                    content = @Content(schema = @Schema(implementation = ResearchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid research query",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal processing failure",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/research")
    public ResponseEntity<ResearchResponse> createResearch(@Valid @RequestBody ResearchRequest request) {
        log.info("Received research request: '{}'", request.getQuery());
        ResearchResponse response = researchService.conductResearch(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get research details by ID", description = "Retrieves full structured research report including citations and sources.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Research report found",
                    content = @Content(schema = @Schema(implementation = ResearchResponse.class))),
            @ApiResponse(responseCode = "404", description = "Research task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/research/{id}")
    public ResponseEntity<ResearchResponse> getResearchById(@PathVariable String id) {
        ResearchResponse response = researchService.getResearchById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all previous research tasks", description = "Returns summary list of all completed or in-progress research topics.")
    @GetMapping("/research")
    public ResponseEntity<List<ResearchSummaryResponse>> getAllResearches() {
        List<ResearchSummaryResponse> history = researchService.getAllResearches();
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Delete a research task", description = "Permanently removes the research record and its collected sources.")
    @DeleteMapping("/research/{id}")
    public ResponseEntity<Map<String, Object>> deleteResearch(@PathVariable String id) {
        researchService.deleteResearch(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Research task " + id + " deleted successfully.");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Service Health Check", description = "Returns current backend status and connectivity.")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "ai-research-agent-backend");
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }
}
