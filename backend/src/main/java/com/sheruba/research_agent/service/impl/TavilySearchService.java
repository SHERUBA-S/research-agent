package com.sheruba.research_agent.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheruba.research_agent.dto.SearchResult;
import com.sheruba.research_agent.exception.SearchException;
import com.sheruba.research_agent.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("tavilySearchService")
public class TavilySearchService implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(TavilySearchService.class);
    private static final String TAVILY_ENDPOINT = "https://api.tavily.com/search";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public TavilySearchService(RestClient restClient,
                               ObjectMapper objectMapper,
                               @Value("${app.search.api-key:}") String apiKey) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey != null ? apiKey.trim() : "";
    }

    @Override
    public List<SearchResult> search(String query, int maxResults) {
        if (!isAvailable()) {
            throw new SearchException("Tavily API key is not configured. Please set SEARCH_API_KEY environment variable.");
        }

        log.info("Executing Tavily search for query: '{}' (max: {})", query, maxResults);

        try {
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("api_key", apiKey);
            requestPayload.put("query", query);
            requestPayload.put("search_depth", "advanced");
            requestPayload.put("include_answer", false);
            requestPayload.put("include_raw_content", false);
            requestPayload.put("max_results", maxResults);

            String responseBody = restClient.post()
                    .uri(TAVILY_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .body(String.class);

            List<SearchResult> results = new ArrayList<>();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode resultsArray = rootNode.path("results");

            if (resultsArray.isArray()) {
                for (JsonNode item : resultsArray) {
                    String title = item.path("title").asText("");
                    String url = item.path("url").asText("");
                    String snippet = item.path("content").asText("");
                    String domain = extractDomain(url);

                    if (!title.isEmpty() && !url.isEmpty()) {
                        results.add(new SearchResult(title, url, snippet, domain));
                    }
                }
            }

            log.info("Tavily returned {} results for query: '{}'", results.size(), query);
            return results;
        } catch (Exception e) {
            log.error("Tavily search failed for query '{}': {}", query, e.getMessage(), e);
            throw new SearchException("Tavily search failed: " + e.getMessage(), e);
        }
    }

    private String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            if (domain != null) {
                return domain.startsWith("www.") ? domain.substring(4) : domain;
            }
        } catch (Exception ignored) {
        }
        return "web";
    }

    @Override
    public String getProviderName() {
        return "tavily";
    }

    @Override
    public boolean isAvailable() {
        return !apiKey.isEmpty();
    }
}
