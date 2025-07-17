package com.example.SkillRoute.GateWay;

import com.example.SkillRoute.config.WebClientConfig;
import com.example.SkillRoute.dto.GeminiRequest;
import com.example.SkillRoute.dto.GeminiResponse;
import com.example.SkillRoute.dto.Quiz_Playground.QuizGenRequest;
import com.example.SkillRoute.dto.Quiz_Playground.QuizGenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.DataInput;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiGateway_Quiz {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    public QuizGenResponse generateQuiz(QuizGenRequest request) {
        String prompt = buildPrompt(request);

        GeminiRequest geminiRequest = new GeminiRequest(
                List.of(
                        new GeminiRequest.Content(
                                List.of(
                                        new GeminiRequest.Part(prompt)
                                )
                        )
                )
        );

        // Send request
        GeminiResponse response = webClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                .bodyValue(geminiRequest)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();

        // Extract text
        System.out.println("Raw Response");
        System.out.println(response);





        try {
            // ✅ Extract JSON string from GeminiResponse
            String jsonText = extractText(response);  // Make sure extractText() is implemented correctly

            System.out.println("Extracted JSON:");
            System.out.println(jsonText);  // Print the clean JSON before parsing

            // ✅ Convert the cleaned JSON into QuizGenResponse object
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonText, QuizGenResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini quiz response", e);
        }
    }

    private String buildPrompt(QuizGenRequest req) {
        StringBuilder sb = new StringBuilder();

        sb.append("You are an API backend assistant.\n");
        sb.append("Generate a quiz with exactly one single-correct multiple-choice question (MCQ) for each of the following topics.\n");
        sb.append("Each question must have exactly four options.\n");
        sb.append("Return only valid JSON, no extra explanation or formatting.\n");
        sb.append("Do NOT include reasoning or Markdown. This will be parsed directly by a backend service.\n");
        sb.append("Format:\n");
        sb.append("{\n");
        sb.append("  \"roadmapId\": ").append(req.getRoadmapId()).append(",\n");
        sb.append("  \"questions\": [\n");

        for (String topic : req.getTopics()) {
            sb.append("    {\n");
            sb.append("      \"topic\": \"").append(topic).append("\",\n");
            sb.append("      \"question\": \"<your generated question>\",\n");
            sb.append("      \"options\": [\"Option A\", \"Option B\", \"Option C\", \"Option D\"],\n");
            sb.append("      \"correctOption\": \"<exact text of correct option>\"\n");
            sb.append("    },\n");
        }

        sb.append("  ]\n");
        sb.append("}");

        return sb.toString();
    }

    private String extractText(GeminiResponse response) {
        try {
            String raw = response.getCandidates()
                    .get(0)
                    .getContent()
                    .getParts()
                    .get(0)
                    .getText()
                    .trim();

            // Remove any prefix like "```json" or "json"
            if (raw.startsWith("```json")) {
                raw = raw.substring(7).trim();
            } else if (raw.startsWith("json")) {
                raw = raw.substring(4).trim();
            } else if (raw.startsWith("```")) {
                raw = raw.substring(3).trim();
            }

            // Remove trailing ```
            if (raw.endsWith("```")) {
                raw = raw.substring(0, raw.length() - 3).trim();
            }

            return raw;

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to extract valid JSON text from Gemini response", e);
        }
    }



}
