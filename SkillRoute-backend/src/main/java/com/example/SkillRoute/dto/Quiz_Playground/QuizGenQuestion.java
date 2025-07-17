package com.example.SkillRoute.dto.Quiz_Playground;

import lombok.Data;

import java.util.List;

@Data
public class QuizGenQuestion {
    private String topic;
    private String question;
    private List<String> options;
    private String correctOption;
}
