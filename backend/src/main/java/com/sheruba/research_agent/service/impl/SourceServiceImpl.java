package com.sheruba.research_agent.service.impl;

import com.sheruba.research_agent.dto.SearchResult;
import com.sheruba.research_agent.dto.SourceDto;
import com.sheruba.research_agent.entity.Research;
import com.sheruba.research_agent.entity.ResearchSource;
import com.sheruba.research_agent.service.SourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;

@Service
public class SourceServiceImpl implements SourceService {

    private static final Logger log = LoggerFactory.getLogger(SourceServiceImpl.class);

    @Override
    public List<SearchResult> deduplicateAndNormalize(List<SearchResult> results) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }

        List<SearchResult> deduped = new ArrayList<>();
        Set<String> seenUrls = new HashSet<>();
        Set<String> seenTitles = new HashSet<>();

        for (SearchResult item : results) {
            String url = item.getUrl();
            if (url == null || url.isBlank() || !url.startsWith("http")) {
                continue;
            }

            String normalizedUrl = normalizeUrl(url);
            String normalizedTitle = item.getTitle() != null ? item.getTitle().trim().toLowerCase() : "";

            if (!seenUrls.contains(normalizedUrl) && !seenTitles.contains(normalizedTitle)) {
                seenUrls.add(normalizedUrl);
                if (!normalizedTitle.isEmpty()) {
                    seenTitles.add(normalizedTitle);
                }
                item.setDomain(extractDomain(url));
                deduped.add(item);
            }
        }

        log.debug("Deduplicated {} sources down to {}", results.size(), deduped.size());
        return deduped;
    }

    @Override
    public List<ResearchSource> createSources(Research research, List<SearchResult> results) {
        List<ResearchSource> sources = new ArrayList<>();
        if (results == null) {
            return sources;
        }

        for (int i = 0; i < results.size(); i++) {
            SearchResult item = results.get(i);
            int citationNum = i + 1;

            ResearchSource source = new ResearchSource();
            source.setId(UUID.randomUUID().toString());
            source.setResearch(research);
            source.setTitle(sanitizeString(item.getTitle(), 990));
            source.setUrl(sanitizeString(item.getUrl(), 2000));
            source.setDomain(extractDomain(item.getUrl()));
            source.setSnippet(item.getSnippet() != null ? item.getSnippet() : "");
            source.setCitationNumber(citationNum);

            sources.add(source);
        }

        return sources;
    }

    @Override
    public List<SourceDto> mapToDtos(List<ResearchSource> sources) {
        if (sources == null) {
            return new ArrayList<>();
        }

        List<SourceDto> dtos = new ArrayList<>();
        for (ResearchSource s : sources) {
            dtos.add(new SourceDto(
                    s.getId(),
                    s.getTitle(),
                    s.getUrl(),
                    s.getDomain(),
                    s.getSnippet(),
                    s.getCitationNumber()
            ));
        }
        return dtos;
    }

    private String normalizeUrl(String url) {
        try {
            URI uri = new URI(url.trim());
            String scheme = uri.getScheme() != null ? uri.getScheme().toLowerCase() : "http";
            String host = uri.getHost() != null ? uri.getHost().toLowerCase() : "";
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            String path = uri.getPath() != null ? uri.getPath() : "";
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            return scheme + "://" + host + path;
        } catch (Exception e) {
            return url.trim().toLowerCase();
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

    private String sanitizeString(String val, int maxLength) {
        if (val == null) return "";
        val = val.trim();
        if (val.length() > maxLength) {
            return val.substring(0, maxLength);
        }
        return val;
    }
}
