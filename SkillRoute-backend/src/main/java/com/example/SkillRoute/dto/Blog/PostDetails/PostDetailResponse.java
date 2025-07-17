package com.example.SkillRoute.dto.Blog.PostDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    private UserSummary user;
    private int upvotes;
    private int downvotes;

    private List<CommentSummary> comments;  // Only top-level comments
}
