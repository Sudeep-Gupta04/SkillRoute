package com.example.SkillRoute.model.Roadmaps;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Youtube {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    @ManyToOne
    @JoinColumn(name = "roadmap_id")  // Foreign key column in Youtube table
    @JsonBackReference
    private Roadmap roadmap;

}

