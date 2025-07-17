package com.example.SkillRoute.repository.Quiz_Playground;

import com.example.SkillRoute.model.Quiz_Playground.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findByUserId(Long userId);  // Optional, for dashboard/history
    Optional<Quiz> findByUserIdAndRoadmapId(Long userId, Long roadmapId);
    List<Quiz> findAllByUserId(Long userId);
}
