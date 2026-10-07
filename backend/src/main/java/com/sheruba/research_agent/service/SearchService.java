package com.sheruba.research_agent.service;

import com.sheruba.research_agent.dto.SearchResult;
import java.util.List;

public interface SearchService {

    /**
     * Executes web search for the given query.
     *
     * @param query The search query or research topic.
     * @param maxResults Maximum number of results to fetch.
     * @return List of SearchResult items.
     */
    List<SearchResult> search(String query, int maxResults);

    /**
     * Returns the name of the search provider.
     */
    String getProviderName();

    /**
     * Indicates whether this provider is currently available/configured.
     */
    boolean isAvailable();
}
