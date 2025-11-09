package com.example.Angler.openai.vision.controller;

import java.io.IOException;

import com.example.Angler.openai.vision.response.ChatGPTResponse;
import com.example.Angler.openai.vision.service.AiCallServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/angler")
@RequiredArgsConstructor
public class OpenAiAPIController {
    private final AiCallServiceImp aiCallServiceImp;

// gpt gpt-4o 모델
    // image + Text 기반 OpenAI 연결 Controller
    @PostMapping("/image")
    public String imageAnalysis(@RequestParam MultipartFile image, @RequestParam String requestText)
            throws IOException {
        ChatGPTResponse response = aiCallServiceImp.requestImageAnalysis(image, requestText);
        return response.getChoices().get(0).getMessage().getContent();
    }

    // Text 기반 OpenAI 연결 Controller
    @PostMapping("/text")
    public String textAnalysis(@RequestParam String requestText) {
        ChatGPTResponse response = aiCallServiceImp.requestTextAnalysis(requestText);
        return response.getChoices().get(0).getMessage().getContent();
    }
}