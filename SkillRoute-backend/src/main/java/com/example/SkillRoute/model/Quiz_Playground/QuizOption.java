package com.example.SkillRoute.model.Quiz_Playground;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "quiz_option")
@Data
public class QuizOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private boolean correct;  // true if this is the right answer

    // Getters and Setters
}