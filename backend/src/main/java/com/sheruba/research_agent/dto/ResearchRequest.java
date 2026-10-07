package com.sheruba.research_agent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResearchRequest {

    @NotBlank(message = "Research query cannot be empty")
    @Size(min = 3, max = 2000, message = "Research query must be between 3 and 2000 characters")
    private String query;

    public ResearchRequest() {
    }

    public ResearchRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
