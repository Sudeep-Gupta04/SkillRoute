package com.example.SkillRoute.GateWay;

import com.example.SkillRoute.dto.ChatCompletionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.SkillRoute.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeepseekService_resource {
    private final WebClient webClient;

    @Value("${huggingface_api_token}")
    private String apiToken;

    public String generateResources(String prompt) throws JsonProcessingException {
        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> requestBody = Map.of(
                "model", "deepseek/deepseek-r1-0528",
                "stream", false,
                "messages", List.of(message)
        );
        String json = webClient.post()
                .uri("https://router.huggingface.co/novita/v3/openai/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("Raw Reponse");
        System.out.println(json);
        System.out.println();

        ObjectMapper objectMapper = new ObjectMapper();
        ChatCompletionResponse response = objectMapper.readValue(json, ChatCompletionResponse.class);

        // extracting the content form the response and returing it
        return response.getChoices().get(0).getMessage().getContent();
    }
}
