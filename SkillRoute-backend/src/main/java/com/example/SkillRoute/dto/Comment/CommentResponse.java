package com.example.SkillRoute.dto.Comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String text;
    private String createdAt;
    private Long postId;
    private Long parentId;
    private int upvotes;
    private int downvotes;
    private int repliesCount;
    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private Long id;
        private String username;
    }
}


