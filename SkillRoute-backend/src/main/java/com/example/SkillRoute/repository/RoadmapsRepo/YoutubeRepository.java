package com.example.SkillRoute.repository.RoadmapsRepo;

import com.example.SkillRoute.model.Roadmaps.Youtube;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YoutubeRepository extends JpaRepository<Youtube,Long> {

}
