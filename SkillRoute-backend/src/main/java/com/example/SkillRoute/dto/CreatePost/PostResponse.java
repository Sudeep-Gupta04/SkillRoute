package com.example.SkillRoute.dto.CreatePost;

import com.example.SkillRoute.dto.Comment.CommentResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostResponse {
    private Long id; // ✅ Add this
    private String title;
    private String content;
    private String createdAt;
    private String imageUrl;
    private UserInfo user;
    private int upvotes;
    private int downvotes;

    private List<CommentResponse> comments;

    @Data
    @Builder
    public static class UserInfo {
        private Long id;
        private String username;
    }
}
