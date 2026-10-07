package com.sheruba.research_agent.service.impl;

import com.sheruba.research_agent.dto.SearchResult;
import com.sheruba.research_agent.service.ContentExtractionService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContentExtractionServiceImpl implements ContentExtractionService {

    private static final Logger log = LoggerFactory.getLogger(ContentExtractionServiceImpl.class);
    private static final int TIMEOUT_MILLIS = 7000;
    private static final int MAX_EXTRACTED_LENGTH = 3500;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

    @Override
    public List<SearchResult> enrichWithContent(List<SearchResult> results, int maxPagesToFetch) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }

        List<SearchResult> enriched = new ArrayList<>();
        int count = 0;

        for (SearchResult result : results) {
            String content = result.getRawContent();

            if ((content == null || content.isBlank()) && count < maxPagesToFetch) {
                try {
                    String extracted = extractText(result.getUrl());
                    if (extracted != null && !extracted.isBlank()) {
                        result.setRawContent(extracted);
                    } else {
                        result.setRawContent(result.getSnippet());
                    }
                    count++;
                } catch (Exception e) {
                    log.debug("Could not extract deep content from {}: {}", result.getUrl(), e.getMessage());
                    result.setRawContent(result.getSnippet());
                }
            } else if (content == null || content.isBlank()) {
                result.setRawContent(result.getSnippet());
            }

            enriched.add(result);
        }

        return enriched;
    }

    @Override
    public String extractText(String url) {
        if (url == null || url.isBlank() || !url.startsWith("http")) {
            return "";
        }

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MILLIS)
                    .followRedirects(true)
                    .maxBodySize(1024 * 1024) // 1MB limit
                    .get();

            // Remove non-content elements
            doc.select("script, style, nav, footer, header, noscript, iframe, svg, form, aside").remove();

            // Prioritize main article / content selectors
            Element mainContent = doc.selectFirst("article, main, .article-content, #content, .post-content, .entry-content");
            Elements paragraphs = (mainContent != null) ? mainContent.select("p, h2, h3, li") : doc.select("p, h2, h3, li");

            StringBuilder sb = new StringBuilder();
            for (Element p : paragraphs) {
                String text = p.text().trim();
                if (text.length() > 30) {
                    sb.append(text).append("\n\n");
                }
                if (sb.length() >= MAX_EXTRACTED_LENGTH) {
                    break;
                }
            }

            String extracted = sb.toString().trim();
            if (extracted.isEmpty()) {
                // Fallback to body text with basic cleanup
                extracted = Jsoup.clean(doc.body().text(), Safelist.none());
                if (extracted.length() > MAX_EXTRACTED_LENGTH) {
                    extracted = extracted.substring(0, MAX_EXTRACTED_LENGTH);
                }
            }

            return extracted;
        } catch (Exception e) {
            log.debug("Extraction failed for {}: {}", url, e.getMessage());
            return "";
        }
    }
}
