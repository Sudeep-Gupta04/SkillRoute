package com.example.SkillRoute.dto.Comment;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private Long postId;     // Required
    private String text;     // Required
    private Long parentId;   // Optional
}
