package com.sheruba.research_agent.service;

import com.sheruba.research_agent.dto.SearchResult;
import java.util.List;

public interface ContentExtractionService {

    /**
     * Enriches search results with extracted page content.
     *
     * @param results List of initial search results.
     * @param maxPagesToFetch Maximum number of URLs to deeply crawl and extract content from.
     * @return Enriched list of SearchResult items with raw content filled in.
     */
    List<SearchResult> enrichWithContent(List<SearchResult> results, int maxPagesToFetch);

    /**
     * Extracts text content from a single URL safely.
     *
     * @param url The webpage URL.
     * @return Extracted and sanitized text content, or empty string on failure.
     */
    String extractText(String url);
}
