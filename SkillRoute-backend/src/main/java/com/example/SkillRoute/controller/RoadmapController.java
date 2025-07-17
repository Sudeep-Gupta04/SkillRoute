package com.example.SkillRoute.controller;

import com.example.SkillRoute.dto.RoadmapDTO;
import com.example.SkillRoute.model.Roadmaps.Roadmap;
import com.example.SkillRoute.model.User;
import com.example.SkillRoute.repository.UserRepository;
import com.example.SkillRoute.service.RoadmapService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadmap")
public class RoadmapController {

    @Autowired
    private  RoadmapService roadmapService;
    @Autowired
    private  UserRepository userRepository;

    public RoadmapController(RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }
    // ✅ Create a roadmap for a specific user
    @PostMapping("/create")
    public ResponseEntity<List<String>> createRoadmap(@RequestParam String title) {

        // ✅ Extract username from JWT (SecurityContext)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // ✅ Fetch userId using the username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        // ✅ Pass userId to service for validation and processing
        List<String> topics = roadmapService.createRoadmap(userId, title);

        return ResponseEntity.ok(topics);
    }



    @GetMapping("/getallroadmaps")
    public ResponseEntity<List<Roadmap>> getRoadmapsByUser() {

        // ✅ Get username from SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // ✅ Lookup userId from username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        // ✅ Pass userId to service
        List<Roadmap> roadmaps = roadmapService.getRoadmapsByUser(userId);

        return ResponseEntity.ok(roadmaps);
    }


    @PutMapping("/topics/{topicId}/complete/")
    public ResponseEntity<String> markTopicComplete(@PathVariable Long topicId) {
        roadmapService.markTopicAsCompleted(topicId);
        return ResponseEntity.ok("Topic marked as completed");
    }
    @DeleteMapping("/delete/{id}/")
    public ResponseEntity<String> deleteRoadmap(@PathVariable Long id) {
        boolean deleted = roadmapService.deleteRoadmap(id);
        if (deleted) {
            return ResponseEntity.ok("Roadmap deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Roadmap not found.");
        }
    }

    @GetMapping("/getroadmap/{id}/")
    public ResponseEntity<Roadmap> getRoadmapById(@PathVariable Long id) {
        Roadmap roadmap = roadmapService.getRoadmapById(id);
        return ResponseEntity.ok(roadmap);
    }

}
