package com.sheruba.research_agent.service.impl;

import com.sheruba.research_agent.dto.AIReportResult;
import com.sheruba.research_agent.dto.SearchResult;
import com.sheruba.research_agent.exception.AIServiceException;
import com.sheruba.research_agent.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class AIServiceFactory implements AIService {

    private static final Logger log = LoggerFactory.getLogger(AIServiceFactory.class);

    private final AIService geminiAIService;
    private final AIService openAIService;
    private final String configuredProvider;
    private final String apiKey;

    public AIServiceFactory(
            @Qualifier("geminiAIService") AIService geminiAIService,
            @Qualifier("openAIService") AIService openAIService,
            @Value("${app.ai.provider:auto}") String configuredProvider,
            @Value("${app.ai.api-key:}") String apiKey) {
        this.geminiAIService = geminiAIService;
        this.openAIService = openAIService;
        this.configuredProvider = configuredProvider != null ? configuredProvider.trim().toLowerCase() : "auto";
        this.apiKey = apiKey != null ? apiKey.trim() : "";
    }

    private AIService resolveService() {
        if ("openai".equalsIgnoreCase(configuredProvider)) {
            return openAIService;
        } else if ("gemini".equalsIgnoreCase(configuredProvider)) {
            return geminiAIService;
        }

        // Auto mode
        if (apiKey.startsWith("sk-")) {
            return openAIService;
        } else if (apiKey.startsWith("AIza") || geminiAIService.isAvailable()) {
            return geminiAIService;
        } else if (openAIService.isAvailable()) {
            return openAIService;
        }

        // Default to Gemini
        return geminiAIService;
    }

    @Override
    public AIReportResult generateResearchReport(String query, List<SearchResult> sources) {
        if (apiKey.isEmpty()) {
            throw new AIServiceException(
                    "AI API key is missing. Please set the AI_API_KEY environment variable (Google Gemini or OpenAI key) in your environment or .env file.");
        }

        AIService delegate = resolveService();
        log.info("Dispatching AI analysis for '{}' to provider: {}", query, delegate.getProviderName());
        return delegate.generateResearchReport(query, sources);
    }

    @Override
    public String getProviderName() {
        return resolveService().getProviderName();
    }

    @Override
    public boolean isAvailable() {
        return !apiKey.isEmpty() && resolveService().isAvailable();
    }
}
