package com.example.Angler.openai.service;

import com.example.Angler.openai.dto.OpenAIResponseDTO;
import reactor.core.publisher.Mono;

public interface OpenAIService {
    Mono<OpenAIResponseDTO> chat(String userMessage);

}
