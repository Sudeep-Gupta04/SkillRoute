package com.example.SkillRoute.dto;

import java.util.List;
import java.util.Map;

public class ParsedResource {
    private final List<String> youtubeLinks;
    private final Map<String, List<String>> articlesMap;

    public ParsedResource(List<String> youtubeLinks, Map<String, List<String>> articlesMap) {
        this.youtubeLinks = youtubeLinks;
        this.articlesMap = articlesMap;
    }

    public List<String> getYoutubeLinks() {
        return youtubeLinks;
    }

    public Map<String, List<String>> getArticlesMap() {
        return articlesMap;
    }
}
