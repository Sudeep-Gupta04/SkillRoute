package com.example.SkillRoute.dto.Blog.PostDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentSummary {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private UserSummary user;
    private int upvotes;
    private int downvotes;
    private int repliesCount;
}
