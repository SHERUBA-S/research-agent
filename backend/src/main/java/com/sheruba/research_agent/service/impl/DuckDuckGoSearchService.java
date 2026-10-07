package com.sheruba.research_agent.service.impl;

import com.sheruba.research_agent.dto.SearchResult;
import com.sheruba.research_agent.exception.SearchException;
import com.sheruba.research_agent.service.SearchService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service("duckDuckGoSearchService")
public class DuckDuckGoSearchService implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(DuckDuckGoSearchService.class);
    private static final String SEARCH_URL = "https://html.duckduckgo.com/html/?q=";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

    @Override
    public List<SearchResult> search(String query, int maxResults) {
        log.info("Executing DuckDuckGo search for query: '{}' (max: {})", query, maxResults);
        List<SearchResult> results = new ArrayList<>();

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            Document doc = Jsoup.connect(SEARCH_URL + encodedQuery)
                    .userAgent(USER_AGENT)
                    .referrer("https://html.duckduckgo.com/")
                    .timeout(12000)
                    .get();

            Elements resultElements = doc.select(".result, .results_links, .web-result");

            for (Element el : resultElements) {
                if (results.size() >= maxResults) {
                    break;
                }

                Element titleLink = el.selectFirst("a.result__a");
                if (titleLink == null) {
                    titleLink = el.selectFirst(".result__title a");
                }
                if (titleLink == null) {
                    titleLink = el.selectFirst("h2 a");
                }

                Element snippetEl = el.selectFirst(".result__snippet, .snippet");

                if (titleLink != null) {
                    String title = titleLink.text().trim();
                    String rawUrl = titleLink.attr("href");
                    String snippet = snippetEl != null ? snippetEl.text().trim() : "";

                    String cleanUrl = extractActualUrl(rawUrl);
                    if (cleanUrl != null && !cleanUrl.isEmpty() && !title.isEmpty()) {
                        String domain = extractDomain(cleanUrl);
                        results.add(new SearchResult(title, cleanUrl, snippet, domain));
                    }
                }
            }

            // Fallback to DuckDuckGo lite if main returned 0 results
            if (results.isEmpty()) {
                log.info("HTML DDG returned 0 results, attempting lite fallback for '{}'", query);
                results.addAll(searchLite(query, maxResults));
            }

            log.info("DuckDuckGo returned {} results for query: '{}'", results.size(), query);
            return results;
        } catch (Exception e) {
            log.error("DuckDuckGo search failed for query: '{}': {}", query, e.getMessage(), e);
            return searchLite(query, maxResults);
        }
    }

    private List<SearchResult> searchLite(String query, int maxResults) {
        List<SearchResult> results = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            Document doc = Jsoup.connect("https://lite.duckduckgo.com/lite/")
                    .data("q", query)
                    .userAgent(USER_AGENT)
                    .referrer("https://lite.duckduckgo.com/")
                    .timeout(10000)
                    .post();

            Elements links = doc.select("a.result-link");
            Elements snippets = doc.select("td.result-snippet");

            for (int i = 0; i < links.size() && results.size() < maxResults; i++) {
                Element link = links.get(i);
                String title = link.text().trim();
                String rawUrl = link.attr("href");
                String snippet = i < snippets.size() ? snippets.get(i).text().trim() : "";

                String cleanUrl = extractActualUrl(rawUrl);
                if (cleanUrl != null && !cleanUrl.isEmpty() && !title.isEmpty()) {
                    String domain = extractDomain(cleanUrl);
                    results.add(new SearchResult(title, cleanUrl, snippet, domain));
                }
            }
        } catch (Exception e) {
            log.warn("DuckDuckGo lite search also failed: {}", e.getMessage());
        }
        return results;
    }

    private String extractActualUrl(String rawUrl) {
        if (rawUrl == null) return null;
        try {
            if (rawUrl.contains("uddg=")) {
                int uddgIndex = rawUrl.indexOf("uddg=");
                String encodedTarget = rawUrl.substring(uddgIndex + 5);
                int ampIndex = encodedTarget.indexOf('&');
                if (ampIndex != -1) {
                    encodedTarget = encodedTarget.substring(0, ampIndex);
                }
                return URLDecoder.decode(encodedTarget, StandardCharsets.UTF_8);
            }
            if (rawUrl.startsWith("//")) {
                return "https:" + rawUrl;
            }
            if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) {
                return rawUrl;
            }
        } catch (Exception e) {
            log.debug("URL decoding error for rawUrl {}: {}", rawUrl, e.getMessage());
        }
        return rawUrl;
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
        return "duckduckgo";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
