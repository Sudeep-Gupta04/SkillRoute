package com.example.SkillRoute.repository.RoadmapsRepo;

import com.example.SkillRoute.model.Roadmaps.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    // You can add custom queries here later if needed
}
