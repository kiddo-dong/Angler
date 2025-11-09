package com.example.Angler.openai.vision.service;

import com.example.Angler.openai.vision.response.ChatGPTResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AiCallService {
    // Text 기반 데이터 전송
    ChatGPTResponse requestTextAnalysis(String requestText);
    // image + Text 기반 데이터 전송
    ChatGPTResponse requestImageAnalysis(MultipartFile image, String requestText)throws IOException;
}
