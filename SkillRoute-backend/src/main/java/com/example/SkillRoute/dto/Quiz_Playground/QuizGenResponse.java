package com.example.SkillRoute.dto.Quiz_Playground;

import lombok.Data;

import java.util.List;

@Data
public class QuizGenResponse {
    private Long roadmapId;
    private List<QuizGenQuestion> questions;
}
