package com.example.SkillRoute.GateWay;

import com.example.SkillRoute.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GeminiHttpService_chat {

    private  final WebClient webClient;


    @Value("${gemini.api.key}")
    private String apiKey;
    public String askGemini(String prompt) {
        String requestBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        { "text": "%s" }
                      ]
                    }
                  ]
                }
                """.formatted(prompt);

        GeminiResponse response = webClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();

        return response
                .getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
    }
}

