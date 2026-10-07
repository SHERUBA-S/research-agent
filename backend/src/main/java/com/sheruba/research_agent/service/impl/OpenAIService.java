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

@Service("openAIService")
public class OpenAIService implements AIService {

    private static final Logger log = LoggerFactory.getLogger(OpenAIService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final String endpoint;

    public OpenAIService(
            RestClient restClient,
            ObjectMapper objectMapper,
            @Value("${app.ai.api-key:}") String apiKey,
            @Value("${app.ai.openai.model:gpt-4o-mini}") String model,
            @Value("${app.ai.openai.endpoint:https://api.openai.com/v1/chat/completions}") String endpoint) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = model != null ? model.trim() : "gpt-4o-mini";
        this.endpoint = endpoint != null ? endpoint.trim() : "https://api.openai.com/v1/chat/completions";
    }

    @Override
    public AIReportResult generateResearchReport(String query, List<SearchResult> sources) {
        if (!isAvailable()) {
            throw new AIServiceException("OpenAI API key is not configured. Please set the AI_API_KEY environment variable.");
        }

        log.info("Generating research report using OpenAI (model: {}) for query: '{}'", model, query);

        try {
            String prompt = buildPrompt(query, sources);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "You are an expert, meticulous AI Research Analyst. You produce strictly structured JSON reports grounded in provided web sources with exact citations [1], [2].");
            messages.add(systemMsg);

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.2);

            Map<String, String> responseFormat = new HashMap<>();
            responseFormat.put("type", "json_object");
            requestBody.put("response_format", responseFormat);

            String responseStr = restClient.post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseOpenAIResponse(responseStr);
        } catch (Exception e) {
            log.error("OpenAI generation failed for query '{}': {}", query, e.getMessage(), e);
            throw new AIServiceException("Failed to generate research report with OpenAI: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String query, List<SearchResult> sources) {
        StringBuilder sb = new StringBuilder();
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

        sb.append("\nINSTRUCTIONS:\n");
        sb.append("1. Ground your report strictly in the sources above. Do NOT invent fake URLs or cite outside sources.\n");
        sb.append("2. When stating facts or claims, insert citations in brackets such as [1] or [2].\n");
        sb.append("3. Output MUST be valid JSON conforming EXACTLY to this schema:\n");
        sb.append("{\n");
        sb.append("  \"title\": \"A concise, professional research title\",\n");
        sb.append("  \"summary\": \"Executive summary of 2-3 paragraphs providing an immediate overview of the research\",\n");
        sb.append("  \"keyFindings\": [\n");
        sb.append("    \"Key finding 1 with citation [1]\",\n");
        sb.append("    \"Key finding 2 with citation [2]\"\n");
        sb.append("  ],\n");
        sb.append("  \"analysis\": \"Comprehensive, multi-paragraph deep-dive analysis covering mechanisms, impacts, trends, and evidence, fully cited with [1], [2], etc.\",\n");
        sb.append("  \"importantInsights\": [\n");
        sb.append("    \"Strategic insight or implication 1\",\n");
        sb.append("    \"Strategic insight or implication 2\"\n");
        sb.append("  ]\n");
        sb.append("}\n");

        return sb.toString();
    }

    private AIReportResult parseOpenAIResponse(String responseStr) {
        try {
            JsonNode root = objectMapper.readTree(responseStr);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                throw new AIServiceException("No choices returned from OpenAI API");
            }

            String content = choices.get(0).path("message").path("content").asText("");
            if (content.isBlank()) {
                throw new AIServiceException("Empty content in OpenAI response choice");
            }

            JsonNode parsedJson = objectMapper.readTree(content);
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
            log.error("Failed to parse OpenAI response: {}. Raw response: {}", e.getMessage(), responseStr);
            throw new AIServiceException("Failed to parse structured response from OpenAI: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "openai";
    }

    @Override
    public boolean isAvailable() {
        return !apiKey.isEmpty();
    }
}
