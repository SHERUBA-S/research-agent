package com.sheruba.research_agent.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheruba.research_agent.dto.AIReportResult;
import com.sheruba.research_agent.dto.SearchResult;
import com.sheruba.research_agent.exception.AIServiceException;
import com.sheruba.research_agent.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("geminiAIService")
public class GeminiAIService implements AIService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAIService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final String endpoint;

    public GeminiAIService(
            RestClient restClient,
            ObjectMapper objectMapper,
            @Value("${app.ai.api-key:}") String apiKey,
            @Value("${app.ai.gemini.model:gemini-flash-lite-latest}") String model,
            @Value("${app.ai.gemini.endpoint:https://generativelanguage.googleapis.com/v1beta/models}") String endpoint) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = model != null ? model.trim() : "gemini-flash-lite-latest";
        this.endpoint = endpoint != null ? endpoint.trim() : "https://generativelanguage.googleapis.com/v1beta/models";
    }

    @Override
    public AIReportResult generateResearchReport(String query, List<SearchResult> sources) {
        if (!isAvailable()) {
            throw new AIServiceException("Gemini API key is not configured. Please set the AI_API_KEY environment variable.");
        }

        log.info("Generating research report using Gemini (model: {}) for query: '{}'", model, query);

        try {
            String prompt = buildPrompt(query, sources);

            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();
            List<Map<String, String>> parts = new ArrayList<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            parts.add(part);
            content.put("parts", parts);
            contents.add(content);
            requestBody.put("contents", contents);

            // Generation config with JSON response mime type
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.2);
            generationConfig.put("responseMimeType", "application/json");
            requestBody.put("generationConfig", generationConfig);

            List<String> candidateModels = new ArrayList<>();
            candidateModels.add(this.model);
            for (String fallback : List.of("gemini-flash-lite-latest", "gemini-3.1-flash-lite", "gemini-3.8-flash", "gemini-3.5-flash", "gemini-flash-latest")) {
                if (!candidateModels.contains(fallback)) {
                    candidateModels.add(fallback);
                }
            }

            Exception lastException = null;
            for (String currentModel : candidateModels) {
                for (int attempt = 1; attempt <= 3; attempt++) {
                    try {
                        String url = String.format("%s/%s:generateContent?key=%s", endpoint, currentModel, apiKey);
                        String responseStr = restClient.post()
                                .uri(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(requestBody)
                                .retrieve()
                                .body(String.class);

                        return parseGeminiResponse(responseStr);
                    } catch (Exception e) {
                        lastException = e;
                        log.warn("Gemini attempt {} with model {} failed: {}. Backing off...", attempt, currentModel, e.getMessage());
                        if (attempt < 3) {
                            try {
                                Thread.sleep(1500L * attempt);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }
            }

            throw lastException != null ? lastException : new AIServiceException("Failed to reach Gemini after multiple attempts");
        } catch (Exception e) {
            log.error("Gemini AI generation failed for query '{}': {}", query, e.getMessage(), e);
            throw new AIServiceException("Failed to generate research report with Gemini: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String query, List<SearchResult> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert, meticulous AI Research Analyst. Conduct thorough research and write a structured, objective report on the following topic.\n\n");
        sb.append("RESEARCH TOPIC: ").append(query).append("\n\n");
        sb.append("COLLECTED SOURCES:\n");

        if (sources == null || sources.isEmpty()) {
            sb.append("No specific web sources found. Provide an objective overview based on verified domain knowledge, noting limitations.\n");
        } else {
            for (int i = 0; i < sources.size(); i++) {
                SearchResult s = sources.get(i);
                int citationNum = i + 1;
                sb.append(String.format("[%d] Title: %s\n", citationNum, s.getTitle()));
                sb.append(String.format("    URL: %s\n", s.getUrl()));
                sb.append(String.format("    Content/Snippet: %s\n\n", s.getRawContent() != null ? s.getRawContent() : s.getSnippet()));
            }
        }

        sb.append("\nRULES & GUIDELINES:\n");
        sb.append("1. Strictly ground your analysis in the provided sources. Do NOT invent URLs or fake sources.\n");
        sb.append("2. When making claims or stating facts, include citation numbers in square brackets like [1], [2] matching the source numbers above.\n");
        sb.append("3. Clearly distinguish verified facts from speculation or uncertainty.\n");
        sb.append("4. Output MUST be valid JSON conforming EXACTLY to the following schema:\n");
        sb.append("{\n");
        sb.append("  \"title\": \"A concise, professional research title\",\n");
        sb.append("  \"summary\": \"Executive summary of 2-3 paragraphs providing an immediate overview of the research\",\n");
        sb.append("  \"keyFindings\": [\n");
        sb.append("    \"Key finding 1 with citation [1]\",\n");
        sb.append("    \"Key finding 2 with citation [2]\",\n");
        sb.append("    \"Key finding 3 with citation [1][3]\"\n");
        sb.append("  ],\n");
        sb.append("  \"analysis\": \"Comprehensive, deep-dive multi-paragraph analysis covering mechanisms, impacts, trends, and evidence, fully cited with [1], [2], etc.\",\n");
        sb.append("  \"importantInsights\": [\n");
        sb.append("    \"Strategic insight or implication 1\",\n");
        sb.append("    \"Strategic insight or implication 2\"\n");
        sb.append("  ]\n");
        sb.append("}\n");

        return sb.toString();
    }

    private AIReportResult parseGeminiResponse(String responseStr) {
        try {
            JsonNode root = objectMapper.readTree(responseStr);
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new AIServiceException("No candidates returned from Gemini API");
            }

            JsonNode textNode = candidates.get(0).path("content").path("parts").get(0).path("text");
            String text = textNode.asText("");

            if (text.isBlank()) {
                throw new AIServiceException("Empty text content in Gemini candidate response");
            }

            // Strip markdown backticks if any
            text = sanitizeJsonText(text);

            JsonNode parsedJson = objectMapper.readTree(text);
            String title = parsedJson.path("title").asText("Research Report");
            String summary = parsedJson.path("summary").asText("");
            String analysis = parsedJson.path("analysis").asText("");

            List<String> keyFindings = new ArrayList<>();
            JsonNode kfNode = parsedJson.path("keyFindings");
            if (kfNode.isArray()) {
                for (JsonNode kf : kfNode) {
                    keyFindings.add(kf.asText());
                }
            }

            List<String> importantInsights = new ArrayList<>();
            JsonNode iiNode = parsedJson.path("importantInsights");
            if (iiNode.isArray()) {
                for (JsonNode ii : iiNode) {
                    importantInsights.add(ii.asText());
                }
            }

            return new AIReportResult(title, summary, keyFindings, analysis, importantInsights);
        } catch (AIServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}. Raw response: {}", e.getMessage(), responseStr);
            throw new AIServiceException("Failed to parse structured response from Gemini: " + e.getMessage(), e);
        }
    }

    private String sanitizeJsonText(String text) {
        text = text.trim();
        if (text.startsWith("```json")) {
            text = text.substring(7);
        } else if (text.startsWith("```")) {
            text = text.substring(3);
        }
        if (text.endsWith("```")) {
            text = text.substring(0, text.length() - 3);
        }
        return text.trim();
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }

    @Override
    public boolean isAvailable() {
        return !apiKey.isEmpty();
    }
}
