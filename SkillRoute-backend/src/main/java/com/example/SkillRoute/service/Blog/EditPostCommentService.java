package com.example.SkillRoute.service.Blog;

import com.example.SkillRoute.dto.Comment.CommentResponse;
import com.example.SkillRoute.dto.CreatePost.PostResponse;
import com.example.SkillRoute.dto.EditPostComment.UpdateCommentRequest;
import com.example.SkillRoute.dto.EditPostComment.UpdatePostRequest;
import com.example.SkillRoute.model.Blog.Comment;
import com.example.SkillRoute.model.Blog.Post;
import com.example.SkillRoute.repository.BlogRepo.CommentRepository;
import com.example.SkillRoute.repository.BlogRepo.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EditPostCommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public PostResponse updatePost(Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        postRepository.save(post);

        return PostResponse.builder()
                .id(post.getId())

                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt().toString())
                .user(PostResponse.UserInfo.builder()
                        .id(post.getUser().getId())
                        .username(post.getUser().getUsername())
                        .build())
                .build();
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.delete(post);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setText(request.getText());
        commentRepository.save(comment);

        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .postId(comment.getPost().getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .createdAt(comment.getCreatedAt().toString())
                .user(CommentResponse.UserInfo.builder()
                        .id(comment.getUser().getId())
                        .username(comment.getUser().getUsername())
                        .build())
                .build();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
    }

}

