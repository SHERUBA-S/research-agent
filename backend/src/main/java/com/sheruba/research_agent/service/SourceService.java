package com.sheruba.research_agent.service;

import com.sheruba.research_agent.dto.SearchResult;
import com.sheruba.research_agent.dto.SourceDto;
import com.sheruba.research_agent.entity.Research;
import com.sheruba.research_agent.entity.ResearchSource;
import java.util.List;

public interface SourceService {

    /**
     * Deduplicates and normalizes search results.
     */
    List<SearchResult> deduplicateAndNormalize(List<SearchResult> results);

    /**
     * Converts search results into persistent ResearchSource entities associated with research.
     */
    List<ResearchSource> createSources(Research research, List<SearchResult> results);

    /**
     * Converts ResearchSource entities to DTOs.
     */
    List<SourceDto> mapToDtos(List<ResearchSource> sources);
}
