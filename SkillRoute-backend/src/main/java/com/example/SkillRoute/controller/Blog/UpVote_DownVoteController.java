package com.example.SkillRoute.controller.Blog;

import com.example.SkillRoute.dto.UpVote_DownVote.VoteRequest;
import com.example.SkillRoute.service.Blog.PostService;
import com.example.SkillRoute.service.Blog.UpVotes_DownVotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class UpVote_DownVoteController {

    @Autowired
    private final UpVotes_DownVotesService UpVotesDownVotesService;

    @PostMapping("/{postId}/vote")
    public ResponseEntity<String> voteOnPost(@PathVariable Long postId, @RequestBody VoteRequest request) {
        UpVotesDownVotesService.voteOnPost(postId, request);
        return ResponseEntity.ok("Vote processed successfully.");
    }


    @PostMapping("/{commentId}/vote")
    public ResponseEntity<String> voteOnComment(@PathVariable Long commentId, @RequestBody VoteRequest request) {
        UpVotesDownVotesService.voteOnComment(commentId, request);
        return ResponseEntity.ok("Vote on comment processed.");
    }
}
