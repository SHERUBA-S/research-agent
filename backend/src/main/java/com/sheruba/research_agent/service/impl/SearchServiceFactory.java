package com.sheruba.research_agent.service.impl;

import com.sheruba.research_agent.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class SearchServiceFactory implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceFactory.class);

    private final SearchService tavilySearchService;
    private final SearchService serperSearchService;
    private final SearchService duckDuckGoSearchService;
    private final String configuredProvider;
    private final String apiKey;

    public SearchServiceFactory(
            @Qualifier("tavilySearchService") SearchService tavilySearchService,
            @Qualifier("serperSearchService") SearchService serperSearchService,
            @Qualifier("duckDuckGoSearchService") SearchService duckDuckGoSearchService,
            @Value("${app.search.provider:auto}") String configuredProvider,
            @Value("${app.search.api-key:}") String apiKey) {
        this.tavilySearchService = tavilySearchService;
        this.serperSearchService = serperSearchService;
        this.duckDuckGoSearchService = duckDuckGoSearchService;
        this.configuredProvider = configuredProvider != null ? configuredProvider.trim().toLowerCase() : "auto";
        this.apiKey = apiKey != null ? apiKey.trim() : "";
    }

    private SearchService resolveService() {
        if ("tavily".equalsIgnoreCase(configuredProvider)) {
            return tavilySearchService;
        } else if ("serper".equalsIgnoreCase(configuredProvider)) {
            return serperSearchService;
        } else if ("duckduckgo".equalsIgnoreCase(configuredProvider)) {
            return duckDuckGoSearchService;
        }

        // Auto mode
        if (!apiKey.isEmpty()) {
            if (apiKey.startsWith("tvly-") || apiKey.startsWith("tvly_")) {
                return tavilySearchService;
            }
            if (tavilySearchService.isAvailable()) {
                return tavilySearchService;
            }
            if (serperSearchService.isAvailable()) {
                return serperSearchService;
            }
        }

        log.debug("Auto-selected DuckDuckGo as search provider");
        return duckDuckGoSearchService;
    }

    @Override
    public List<com.sheruba.research_agent.dto.SearchResult> search(String query, int maxResults) {
        SearchService delegate = resolveService();
        log.info("Dispatching web search for '{}' to provider: {}", query, delegate.getProviderName());
        try {
            return delegate.search(query, maxResults);
        } catch (Exception e) {
            if (delegate != duckDuckGoSearchService) {
                log.warn("Primary search provider {} failed: {}. Falling back to DuckDuckGo", delegate.getProviderName(), e.getMessage());
                return duckDuckGoSearchService.search(query, maxResults);
            }
            throw e;
        }
    }

    @Override
    public String getProviderName() {
        return resolveService().getProviderName();
    }

    @Override
    public boolean isAvailable() {
        return resolveService().isAvailable();
    }
}
