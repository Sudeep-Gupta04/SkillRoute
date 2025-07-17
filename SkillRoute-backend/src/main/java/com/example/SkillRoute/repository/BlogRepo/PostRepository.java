package com.example.SkillRoute.repository.BlogRepo;

import com.example.SkillRoute.model.Blog.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Post> searchByTitle(@Param("search") String search);
}
