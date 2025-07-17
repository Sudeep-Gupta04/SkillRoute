package com.example.SkillRoute.repository.Quiz_Playground;

import com.example.SkillRoute.model.Quiz_Playground.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, UUID> {
}
