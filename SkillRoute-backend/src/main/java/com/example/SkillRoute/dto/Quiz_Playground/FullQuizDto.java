package com.example.SkillRoute.dto.Quiz_Playground;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class FullQuizDto {
    private UUID quizId;
    private Long roadmapId;
    private String roadmapTitle;
    private Integer score;
    private LocalDateTime createdAt;
    private List<QuestionDto> questions;

    @Data
    public static class QuestionDto {
        private String question;
        private List<String> options;
        private String userAnswer;
        private String correctAnswer;
    }
}
