package com.example.SkillRoute.controller;

import com.example.SkillRoute.GateWay.GeminiHttpService_chat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")

public class ChatController {

    private final GeminiHttpService_chat geminiHttpServiceChat;

    public ChatController(GeminiHttpService_chat geminiHttpServiceChat) {
        this.geminiHttpServiceChat = geminiHttpServiceChat;
    }


    @PostMapping("/ask/")
    public ResponseEntity<String> ask(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String response = geminiHttpServiceChat.askGemini(prompt);
        return ResponseEntity.ok(response);
    }
}


