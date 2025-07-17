package com.example.SkillRoute.dto.Quiz_Playground;

import lombok.Data;

import java.util.List;

@Data
public class QuizGenRequest {
    private Long roadmapId;
    private List<String> topics;
}
