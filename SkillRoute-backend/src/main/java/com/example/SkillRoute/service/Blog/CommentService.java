package com.example.SkillRoute.service.Blog;

import com.example.SkillRoute.dto.Comment.CommentResponse;
import com.example.SkillRoute.dto.Comment.CreateCommentRequest;
import com.example.SkillRoute.model.Blog.Comment;
import com.example.SkillRoute.model.Blog.Post;
import com.example.SkillRoute.model.Blog.VoteType;
import com.example.SkillRoute.model.User;
import com.example.SkillRoute.repository.BlogRepo.CommentRepository;
import com.example.SkillRoute.repository.BlogRepo.PostRepository;
import com.example.SkillRoute.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    @Autowired
    PostRepository postRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    CommentRepository commentRepo;


    public CommentResponse createComment(Long userId, CreateCommentRequest request) {
        Post post = postRepo.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        if (request.getParentId() != null) {
            Comment parent = commentRepo.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParent(parent);
        }

        Comment saved = commentRepo.save(comment);

        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt().toString())
                .postId(comment.getPost().getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .user(CommentResponse.UserInfo.builder()
                        .id(comment.getUser().getId())
                        .username(comment.getUser().getUsername())
                        .build())
                .build();
    }

    public List<CommentResponse> getReplies(Long parentId) {
        List<Comment> replies = commentRepo.findByParentId(parentId);

        return replies.stream().map(comment -> {
            int upvotes = (int) comment.getCommentVotes().stream()
                    .filter(v -> v.getVoteType() == VoteType.UPVOTE).count();
            int downvotes = (int) comment.getCommentVotes().stream()
                    .filter(v -> v.getVoteType() == VoteType.DOWNVOTE).count();
            int repliesCount = comment.getReplies() != null ? comment.getReplies().size() : 0;

            return CommentResponse.builder()
                    .id(comment.getId())
                    .text(comment.getText())
                    .createdAt(comment.getCreatedAt().toString())
                    .postId(comment.getPost().getId())
                    .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                    .upvotes(upvotes)
                    .downvotes(downvotes)
                    .repliesCount(repliesCount)
                    .user(CommentResponse.UserInfo.builder()
                            .id(comment.getUser().getId())
                            .username(comment.getUser().getUsername())
                            .build())
                    .build();
        }).toList();
    }
}
