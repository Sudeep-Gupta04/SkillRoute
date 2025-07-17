package com.example.SkillRoute.dto.UpVote_DownVote;

import com.example.SkillRoute.model.Blog.VoteType;
import lombok.Data;

@Data
public class VoteRequest {
    private Long userId;
    private VoteType voteType; // UPVOTE or DOWNVOTE
}
