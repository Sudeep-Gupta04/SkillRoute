package com.example.SkillRoute.dto.Quiz_Playground;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuizSubmissionRequest {
    private UUID quizId;
    private List<QuestionAnswer> answers;

    @Data
    public static class QuestionAnswer {
        private UUID questionId;
        private String selected;
    }
}