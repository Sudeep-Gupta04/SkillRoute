package com.example.SkillRoute.repository.RoadmapsRepo;

import com.example.SkillRoute.model.Roadmaps.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {
}
