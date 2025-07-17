package com.example.SkillRoute.controller.Blog;

import com.example.SkillRoute.dto.Comment.CommentResponse;
import com.example.SkillRoute.dto.Comment.CreateCommentRequest;
import com.example.SkillRoute.model.User;
import com.example.SkillRoute.repository.UserRepository;
import com.example.SkillRoute.service.Blog.CommentService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    private final CommentService commentService;
    @Autowired
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CreateCommentRequest request) {

        // ✅ Extract username from JWT via SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // ✅ Fetch userId using username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        // ✅ Pass userId to the service
        CommentResponse response = commentService.createComment(userId, request);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentResponse>> getReplies(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getReplies(commentId));
    }


}