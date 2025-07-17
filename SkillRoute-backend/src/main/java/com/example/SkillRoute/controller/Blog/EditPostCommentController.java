package com.example.SkillRoute.controller.Blog;

import com.example.SkillRoute.dto.Comment.CommentResponse;
import com.example.SkillRoute.dto.CreatePost.PostResponse;
import com.example.SkillRoute.dto.EditPostComment.UpdateCommentRequest;
import com.example.SkillRoute.dto.EditPostComment.UpdatePostRequest;
import com.example.SkillRoute.service.Blog.EditPostCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edit")

public class EditPostCommentController {
    private EditPostCommentService editPostCommentService;

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest request
    ) {
        return ResponseEntity.ok(editPostCommentService.updatePost(postId, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        editPostCommentService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request
    ) {
        return ResponseEntity.ok(editPostCommentService.updateComment(commentId, request));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        editPostCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
