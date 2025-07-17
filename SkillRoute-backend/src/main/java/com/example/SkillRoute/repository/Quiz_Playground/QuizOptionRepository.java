package com.example.SkillRoute.repository.Quiz_Playground;

import com.example.SkillRoute.model.Quiz_Playground.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, UUID> {
}

