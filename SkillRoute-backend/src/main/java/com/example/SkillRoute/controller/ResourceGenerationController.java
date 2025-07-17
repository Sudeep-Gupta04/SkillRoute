package com.example.SkillRoute.controller;

import com.example.SkillRoute.dto.ResourceResponseDTO;
import com.example.SkillRoute.model.Roadmaps.Roadmap;
import com.example.SkillRoute.repository.RoadmapsRepo.RoadmapRepository;
import com.example.SkillRoute.service.ResourceGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceGenerationController {

    private final ResourceGenerationService resourceGenerationService;
    private final RoadmapRepository roadmapRepository;

    // POST /api/resources/generate/{roadmapId}
    @PostMapping("/generate/{roadmapId}")
    public ResponseEntity<?> generateResources(@PathVariable Long roadmapId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId).orElse(null);
        if (roadmap == null) {
            return ResponseEntity.badRequest().body("Roadmap not found with ID: " + roadmapId);
        }

        try {
            ResourceResponseDTO response = resourceGenerationService.enrichRoadmapWithLinks(roadmap);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error generating resources: " + ex.getMessage());
        }
    }
}