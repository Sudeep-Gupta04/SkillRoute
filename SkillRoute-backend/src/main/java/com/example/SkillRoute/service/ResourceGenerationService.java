package com.example.SkillRoute.service;

import com.example.SkillRoute.GateWay.DeepseekService_resource;
import com.example.SkillRoute.dto.ResourceResponseDTO;
import com.example.SkillRoute.model.Roadmaps.Article;
import com.example.SkillRoute.model.Roadmaps.Roadmap;
import com.example.SkillRoute.model.Roadmaps.Topic;
import com.example.SkillRoute.model.Roadmaps.Youtube;
import com.example.SkillRoute.repository.RoadmapsRepo.ArticleRepository;
import com.example.SkillRoute.repository.RoadmapsRepo.YoutubeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ResourceGenerationService {

    @Autowired
    private DeepseekService_resource deepSeekService;

    @Autowired
    private YoutubeRepository youtuberepo;

    @Autowired
    private ArticleRepository articlrepo;

    public String buildPromptFromRoadmap(Roadmap roadmap) {
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("You are an API responding with machine-readable JSON-like output.\n")
                .append("STRICTLY follow this format — DO NOT explain anything or give me  include any extra text.\n")
                .append("Format:\n")
                .append("{\n")
                .append("  \"title\": [\n")
                .append("    \"YouTube URL 1\",\n")
                .append("    \"YouTube URL 2\",\n")
                .append("    \"YouTube URL 3\"\n")
                .append("  ],\n")
                .append("  \"topics\": {\n");

        List<Topic> topics = roadmap.getTopics();
        for (int i = 0; i < topics.size(); i++) {
            String name = topics.get(i).getName().trim();
            promptBuilder.append("    \"").append(name).append("\": [\n")
                    .append("      \"article URL 1\",\n")
                    .append("      \"article URL 2\",\n")
                    .append("      \"article URL 3\"\n")
                    .append("    ]");
            if (i != topics.size() - 1) promptBuilder.append(",");
            promptBuilder.append("\n");
        }

        promptBuilder.append("  }\n")
                .append("}\n")
                .append("\nIMPORTANT RULES:\n")
                .append("- Return ONLY the JSON-like object in the EXACT format above.\n")
                .append("- DO NOT use <think>, bullet points, explanations, or headings.\n")
                .append("- Replace placeholders with real URLs (only up to 3 per section).\n")
                .append("- Ensure everything is valid JSON-like structure, with proper quotes and brackets.");

        return promptBuilder.toString();
    }


    public ResourceResponseDTO enrichRoadmapWithLinks(Roadmap roadmap) throws JsonProcessingException {
        validateExistingResources(roadmap);

        String prompt = buildPromptFromRoadmap(roadmap);
        String content = deepSeekService.generateResources(prompt);
        System.out.println(content);

        ParsedResource parsed = parseRawResponse(content);

        System.out.println("Parsed YouTube Links: " + parsed.getYoutubeLinks());
        System.out.println("Parsed Articles Map: " + parsed.getArticlesMap());

        saveYoutubeLinks(parsed.getYoutubeLinks(), roadmap);
        saveArticleLinks(parsed.getArticlesMap(), roadmap);

        return new ResourceResponseDTO(
                roadmap.getId(),
                parsed.getYoutubeLinks(),
                parsed.getArticlesMap()
        );

    }


    private void validateExistingResources(Roadmap roadmap) {
        if (roadmap.getYoutube() != null && !roadmap.getYoutube().isEmpty()) {
            throw new IllegalStateException("YouTube links already exist for this roadmap.");
        }

        boolean hasArticles = roadmap.getTopics().stream()
                .anyMatch(topic -> topic.getArticles() != null && !topic.getArticles().isEmpty());

        if (hasArticles) {
            throw new IllegalStateException("Articles already exist for topics in this roadmap.");
        }
    }

    private ParsedResource parseRawResponse(String rawResponse) {
        // Extract JSON block from "content": "...", if needed
        int startIndex = rawResponse.indexOf('{');
        int endIndex = rawResponse.lastIndexOf('}');
        if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
            throw new IllegalArgumentException("Invalid content block format");
        }

        String jsonBlock = rawResponse.substring(startIndex, endIndex + 1);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonBlock);

            // Get YouTube links
            List<String> youtubeLinks = new ArrayList<>();
            JsonNode ytArray = root.path("title");
            if (ytArray.isArray()) {
                for (JsonNode yt : ytArray) {
                    if (isValidUrl(yt.asText())) youtubeLinks.add(yt.asText());
                }
            }

            // Get Articles map
            Map<String, List<String>> articlesMap = new LinkedHashMap<>();
            JsonNode topics = root.path("topics");
            if (topics.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = topics.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    String topic = entry.getKey();
                    List<String> urls = new ArrayList<>();

                    for (JsonNode urlNode : entry.getValue()) {
                        String url = urlNode.asText().trim();
                        if (isValidUrl(url)) urls.add(url);
                    }

                    if (!urls.isEmpty()) {
                        articlesMap.put(topic, urls);
                    }
                }
            }

            return new ParsedResource(youtubeLinks, articlesMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse DeepSeek response", e);
        }
    }


    private boolean isValidUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void saveYoutubeLinks(List<String> youtubeLinks, Roadmap roadmap) {
        for (Topic topic : roadmap.getTopics()) {
            for (String url : youtubeLinks) {
                Youtube yt = new Youtube();
                yt.setUrl(url);
                yt.setRoadmap(roadmap); // ✅ set topic, not roadmap
                youtuberepo.save(yt);
            }
        }
    }

    private void saveArticleLinks(Map<String, List<String>> articlesMap, Roadmap roadmap) {
        for (Map.Entry<String, List<String>> entry : articlesMap.entrySet()) {
            String topicKey = entry.getKey();
            List<String> urls = entry.getValue();

            Topic matchedTopic = findMatchingTopic(topicKey, roadmap.getTopics());
            if (matchedTopic == null) {
                System.out.println("⚠️ No match found for topic: " + topicKey);
                continue;
            }

            for (String url : urls) {
                Article article = new Article();
                article.setUrl(url);
                article.setTopic(matchedTopic);
                articlrepo.save(article);
            }
        }
    }


    // Helper class for parsed data
    private static class ParsedResource {
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
    private Topic findMatchingTopic(String topicName, List<Topic> roadmapTopics) {
        String cleanedInput = topicName.toLowerCase().replaceAll("[^a-z0-9]", "");

        for (Topic topic : roadmapTopics) {
            String cleanedRoadmapTopic = topic.getName().toLowerCase().replaceAll("[^a-z0-9]", "");
            if (cleanedInput.equals(cleanedRoadmapTopic) || cleanedInput.contains(cleanedRoadmapTopic) || cleanedRoadmapTopic.contains(cleanedInput)) {
                return topic;
            }
        }
        return null; // no match
    }

}

