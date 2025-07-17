package com.example.SkillRoute.model.Roadmaps;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3000)
    private String name;

    @Column(nullable = false)
    private boolean completed = false;

    @ManyToOne
    @JoinColumn(name = "roadmap_id")
    @JsonIgnore
    private Roadmap roadmap;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Article> articles;
}

