package com.example.SkillRoute.repository.BlogRepo;

import com.example.SkillRoute.model.Blog.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {

    List<CommentVote> findByUserId(Long userId);

    Optional<CommentVote> findByUserIdAndCommentId(Long userId, Long commentId);
}
