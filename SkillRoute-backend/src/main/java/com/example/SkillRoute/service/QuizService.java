package com.example.SkillRoute.service;

import com.example.SkillRoute.GateWay.GeminiGateway_Quiz;
import com.example.SkillRoute.dto.Quiz_Playground.*;
import com.example.SkillRoute.model.Quiz_Playground.Quiz;
import com.example.SkillRoute.model.Quiz_Playground.QuizOption;
import com.example.SkillRoute.model.Quiz_Playground.QuizQuestion;
import com.example.SkillRoute.model.Roadmaps.Roadmap;
import com.example.SkillRoute.model.Roadmaps.Topic;
import com.example.SkillRoute.repository.Quiz_Playground.QuizOptionRepository;
import com.example.SkillRoute.repository.Quiz_Playground.QuizQuestionRepository;
import com.example.SkillRoute.repository.Quiz_Playground.QuizRepository;
import com.example.SkillRoute.repository.RoadmapsRepo.RoadmapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class QuizService {

    @Autowired
    private final GeminiGateway_Quiz geminiGateway;
    @Autowired
    private final RoadmapRepository roadmapRepo;
    @Autowired
    private final QuizRepository quizRepo;
    @Autowired
    private final QuizQuestionRepository questionRepo;
    @Autowired
    private final QuizOptionRepository optionRepo;


    public QuizGenResponse generateQuizFromRoadmap(Long roadmapId) {
        Roadmap roadmap = roadmapRepo.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));
        // if already a quize is created for a roadmap return error that quiz already exist

        if (quizRepo.findByUserIdAndRoadmapId(roadmap.getUser().getId(), roadmapId).isPresent()) {
            throw new RuntimeException("A quiz for this roadmap already exists for the user.");
        }

        List <Topic> topicentity = roadmap.getTopics();
        List<String> topicTitles = new ArrayList<>();
        for(Topic i:topicentity){
            topicTitles.add(i.getName());
        }

        QuizGenRequest req = new QuizGenRequest();
        req.setRoadmapId(roadmapId);
        req.setTopics(topicTitles);
        QuizGenResponse generated  = geminiGateway.generateQuiz(req);

        saveQuizFromGemini(generated, roadmap.getUser().getId());

        return generated;
    }

    @Transactional
    public Quiz saveQuizFromGemini(QuizGenResponse quizData, Long userId) {
        Quiz quiz = new Quiz();
        quiz.setUserId(userId);
        quiz.setRoadmapId(quizData.getRoadmapId());
        quiz.setCreatedAt(LocalDateTime.now());

        List<QuizQuestion> questionEntities = new ArrayList<>();

        int sequence = 1;
        for (QuizGenQuestion q : quizData.getQuestions()) {
            QuizQuestion question = new QuizQuestion();
            question.setQuiz(quiz);
            question.setQuestion(q.getQuestion());
            question.setSequence(sequence++);
            question.setOptions(new ArrayList<>());

            for (String opt : q.getOptions()) {
                QuizOption option = new QuizOption();
                option.setText(opt);
                option.setCorrect(opt.equals(q.getCorrectOption()));
                option.setQuestion(question);
                question.getOptions().add(option);
            }

            questionEntities.add(question);
        }

        quiz.setQuestions(questionEntities);

        return quizRepo.save(quiz);
    }

    @Transactional
    public QuizSubmissionResponse submitQuiz(QuizSubmissionRequest req) {
        Quiz quiz = quizRepo.findById(req.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Map answers by question ID
        Map<UUID, String> submittedAnswers = req.getAnswers()
                .stream()
                .collect(Collectors.toMap(QuizSubmissionRequest.QuestionAnswer::getQuestionId,
                        QuizSubmissionRequest.QuestionAnswer::getSelected));

        int correct = 0;
        for (QuizQuestion question : quiz.getQuestions()) {
            String userAns = submittedAnswers.get(question.getId());
            question.setUserAnswer(userAns); // ✅ save selected answer

            // Check if selected answer matches the correct one
            for (QuizOption option : question.getOptions()) {
                if (option.isCorrect() && option.getText().equals(userAns)) {
                    correct++;
                    break;
                }
            }
        }

        // Save score
        quiz.setScore(correct);
        quizRepo.save(quiz);  // ✅ cascade saves user answers

        // Response
        QuizSubmissionResponse res = new QuizSubmissionResponse();
        res.setTotalQuestions(quiz.getQuestions().size());
        res.setCorrectAnswers(correct);
        res.setScore(correct);

        return res;
    }

    public List<FullQuizDto> getFullQuizzesByUser(Long userId) {
        List<Quiz> quizzes = quizRepo.findAllByUserId(userId);

        return quizzes.stream().map(q -> {
            FullQuizDto dto = new FullQuizDto();
            dto.setQuizId(q.getId());
            dto.setRoadmapId(q.getRoadmapId());
            dto.setScore(q.getScore());
            dto.setCreatedAt(q.getCreatedAt());

            // Get roadmap title
            roadmapRepo.findById(q.getRoadmapId()).ifPresent(r -> {
                dto.setRoadmapTitle(r.getTitle());
            });

            // Build question list
            List<FullQuizDto.QuestionDto> qList = new ArrayList<>();
            for (QuizQuestion question : q.getQuestions()) {
                FullQuizDto.QuestionDto qdto = new FullQuizDto.QuestionDto();
                qdto.setQuestion(question.getQuestion());
                qdto.setUserAnswer(question.getUserAnswer());

                List<String> options = question.getOptions().stream()
                        .map(QuizOption::getText)
                        .toList();
                qdto.setOptions(options);

                // Get correct option
                question.getOptions().stream()
                        .filter(QuizOption::isCorrect)
                        .findFirst()
                        .ifPresent(opt -> qdto.setCorrectAnswer(opt.getText()));

                qList.add(qdto);
            }

            dto.setQuestions(qList);
            return dto;
        }).toList();
    }

    @Transactional
    public void resetQuizAnswers(UUID quizId, Long userId) throws AccessDeniedException {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Reject if quiz doesn't belong to the requesting user
        if (!quiz.getUserId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to reset this quiz.");
        }

        quiz.setScore(null);
        for (QuizQuestion q : quiz.getQuestions()) {
            q.setUserAnswer(null);
        }
        quizRepo.save(quiz);
    }
}
