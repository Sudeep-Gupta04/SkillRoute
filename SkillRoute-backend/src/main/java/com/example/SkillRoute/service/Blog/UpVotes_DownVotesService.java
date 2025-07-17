package com.example.SkillRoute.service.Blog;

import com.example.SkillRoute.dto.UpVote_DownVote.VoteRequest;
import com.example.SkillRoute.model.Blog.Comment;
import com.example.SkillRoute.model.Blog.CommentVote;
import com.example.SkillRoute.model.Blog.Post;
import com.example.SkillRoute.model.Blog.PostVote;
import com.example.SkillRoute.model.User;
import com.example.SkillRoute.repository.BlogRepo.CommentRepository;
import com.example.SkillRoute.repository.BlogRepo.CommentVoteRepository;
import com.example.SkillRoute.repository.BlogRepo.PostRepository;
import com.example.SkillRoute.repository.BlogRepo.PostVoteRepository;
import com.example.SkillRoute.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpVotes_DownVotesService {

    @Autowired
    UserRepository userRepo;


    @Autowired
    CommentRepository commentRepo;

    @Autowired
    CommentVoteRepository commentVoteRepo;
    @Autowired
    PostRepository postRepo;
    @Autowired
    PostVoteRepository postvoteRepo;

    public void voteOnPost(Long postId, VoteRequest request) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<PostVote> existingVoteOpt = postvoteRepo.findByUserIdAndPostId(postId, user.getId());

        if (existingVoteOpt.isPresent()) {
            PostVote existingVote = existingVoteOpt.get();
            if (existingVote.getVoteType() == request.getVoteType()) {
                // same vote again → remove vote (toggle off)
                postvoteRepo.delete(existingVote);
            } else {
                // change vote
                existingVote.setVoteType(request.getVoteType());
                postvoteRepo.save(existingVote);
            }
        } else {
            // new vote
            PostVote newVote = PostVote.builder()
                    .post(post)
                    .user(user)
                    .voteType(request.getVoteType())
                    .build();
            postvoteRepo.save(newVote);
        }
    }

    @Transactional
    public void voteOnComment(Long commentId, VoteRequest request) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<CommentVote> existingVoteOpt = commentVoteRepo.findByUserIdAndCommentId(user.getId(),commentId);

        if (existingVoteOpt.isPresent()) {
            CommentVote existingVote = existingVoteOpt.get();
            if (existingVote.getVoteType() == request.getVoteType()) {
                commentVoteRepo.delete(existingVote); // same vote → remove
            } else {
                existingVote.setVoteType(request.getVoteType()); // change vote
                commentVoteRepo.save(existingVote);
            }
        } else {
            CommentVote newVote = CommentVote.builder()
                    .comment(comment)
                    .user(user)
                    .voteType(request.getVoteType())
                    .build();
            commentVoteRepo.save(newVote);
        }
    }
}
