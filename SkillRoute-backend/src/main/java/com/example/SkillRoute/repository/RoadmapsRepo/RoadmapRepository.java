package com.example.SkillRoute.repository.RoadmapsRepo;

import com.example.SkillRoute.model.Roadmaps.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    List<Roadmap> findByUserId(Long userId);
    void deleteById(Long id);
}
