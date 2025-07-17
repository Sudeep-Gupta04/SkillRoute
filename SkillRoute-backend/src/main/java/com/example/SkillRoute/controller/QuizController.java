package com.example.SkillRoute.controller;

import com.example.SkillRoute.dto.Quiz_Playground.FullQuizDto;
import com.example.SkillRoute.dto.Quiz_Playground.QuizGenResponse;
import com.example.SkillRoute.dto.Quiz_Playground.QuizSubmissionRequest;
import com.example.SkillRoute.dto.Quiz_Playground.QuizSubmissionResponse;
//import com.example.SkillRoute.service.QuizService;
import com.example.SkillRoute.model.User;
import com.example.SkillRoute.repository.UserRepository;
import com.example.SkillRoute.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final UserRepository userRepository;
    @GetMapping("/generate/{roadmapId}")
    public ResponseEntity<QuizGenResponse> generateQuiz(@PathVariable Long roadmapId) {
        QuizGenResponse response = quizService.generateQuizFromRoadmap(roadmapId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/submit")
    public ResponseEntity<QuizSubmissionResponse> submitQuiz(
            @RequestBody QuizSubmissionRequest req) {
        return ResponseEntity.ok(quizService.submitQuiz(req));
    }

    @GetMapping("/user/allQuizes")
    public ResponseEntity<List<FullQuizDto>> getUserFullQuizzes() {

        // ✅ Extract username from JWT via SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // ✅ Fetch userId using username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        // ✅ Pass userId to service
        return ResponseEntity.ok(quizService.getFullQuizzesByUser(userId));
    }

    // reponse
//[
//  {
//    "quizId": "abcd-uuid-1",
//    "roadmapId": 5,
//    "roadmapTitle": "C",
//    "score": 8,
//    "createdAt": "2025-07-03T22:30:00",
//    "questions": [
//      {
//        "question": "Which language influenced C?",
//        "options": ["FORTRAN", "Pascal", "B", "ALGOL"],
//        "userAnswer": "Pascal",
//        "correctAnswer": "B"
//      },
//      ...
//    ]
//  },
//  ...
//]

    @PutMapping("/reset/{quizId}")
    public ResponseEntity<String> resetQuiz(
            @PathVariable UUID quizId,
            @RequestParam Long userId) throws AccessDeniedException {

        quizService.resetQuizAnswers(quizId, userId);
        return ResponseEntity.ok("Quiz reset successfully.");
    }

}
