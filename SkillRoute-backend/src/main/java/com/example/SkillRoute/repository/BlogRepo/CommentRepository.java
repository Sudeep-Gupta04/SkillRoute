package com.example.SkillRoute.repository.BlogRepo;
import com.example.SkillRoute.model.Blog.Comment;
import com.example.SkillRoute.model.Blog.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Comment> findByParentId(Long parentId);

}
