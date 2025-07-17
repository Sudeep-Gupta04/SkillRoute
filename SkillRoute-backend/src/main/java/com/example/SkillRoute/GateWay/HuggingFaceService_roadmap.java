package com.example.SkillRoute.GateWay;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HuggingFaceService_roadmap {

    private final WebClient webClient;

    @Value("${huggingface_api_token}")
    private String apiToken;

    public String generateTopics(String prompt) {
        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> requestBody = Map.of(
                "model", "mistralai/mistral-7b-instruct",
                "stream", false,
                "messages", List.of(message)
        );

        return webClient.post()
                .uri("https://router.huggingface.co/novita/v3/openai/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
