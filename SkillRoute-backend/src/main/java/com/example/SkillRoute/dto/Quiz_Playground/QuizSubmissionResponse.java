package com.example.SkillRoute.dto.Quiz_Playground;

import lombok.Data;

@Data
public class QuizSubmissionResponse {
    private int totalQuestions;
    private int correctAnswers;
    private int score;
}