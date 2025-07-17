package com.example.SkillRoute.service;
import com.example.SkillRoute.GateWay.HuggingFaceService_roadmap;
import com.example.SkillRoute.model.Roadmaps.Roadmap;
import com.example.SkillRoute.model.Roadmaps.Topic;
import com.example.SkillRoute.model.User;
import com.example.SkillRoute.repository.RoadmapsRepo.RoadmapRepository;
import com.example.SkillRoute.repository.UserRepository;
import com.example.SkillRoute.repository.RoadmapsRepo.TopicRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final UserRepository userRepo;
    private final RoadmapRepository roadmapRepo;
    private final TopicRepository topicRepo;
    private final HuggingFaceService_roadmap huggingFaceServiceReadmap;

    public void markTopicAsCompleted(Long topicId) {
        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        topic.setCompleted(true);
        topicRepo.save(topic);
    }

    public boolean deleteRoadmap(Long id) {
        if (roadmapRepo.existsById(id)) {
            roadmapRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public Roadmap getRoadmapById(Long id) {
        return roadmapRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Roadmap not found with id: " + id));
    }

    public List<String> createRoadmap(Long userId, String title) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Roadmap roadmap = new Roadmap();
        roadmap.setTitle(title);
        roadmap.setStartDate(LocalDate.now());
        roadmap.setUser(user);
        roadmap = roadmapRepo.save(roadmap);

        // Call AI for topics
        String prompt = "Provide a numbered list of up to 10 main topic headings that fully cover the subject: "
                + title
                + ". Each heading should represent a major section of the subject. Do not include any explanations or subtopics — only the main topic headings.";

        String rawOutput = huggingFaceServiceReadmap.generateTopics(prompt);
        List<String> topics = extractTopicsFromOutput(rawOutput,title);

        // Save each topic
        for (String topicName : topics) {
            Topic topic = new Topic();
            topic.setName(topicName.trim());
            topic.setRoadmap(roadmap);
            topicRepo.save(topic);
        }

        return topics;
    }

    public List<Roadmap> getRoadmapsByUser(Long userId) {
        return roadmapRepo.findByUserId(userId);
    }

    private List<String> extractTopicsFromOutput(String rawJson,String title) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawJson);

            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                throw new RuntimeException("Missing choices in response");
            }

            JsonNode message = choices.get(0).path("message");
            String content = message.path("content").asText();

            return Arrays.stream(content.split("\n"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(line -> line.replaceFirst("^[0-9]+\\.\\s*", ""))  // Remove "1. ", "2. ", etc.
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // fallback
        return List.of(title+" Introduction",title + " Concepts",title + " Practice",title +" Optimization");
    }

}
