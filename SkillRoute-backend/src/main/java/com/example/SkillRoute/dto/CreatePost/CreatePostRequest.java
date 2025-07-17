package com.example.SkillRoute.dto.CreatePost;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String title;
    private String content;
    private Long roadmapId; // optional
    private Long topicId;   // optional
}
