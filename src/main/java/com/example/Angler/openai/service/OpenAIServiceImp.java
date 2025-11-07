package com.example.Angler.openai.service;

import com.example.Angler.openai.dto.OpenAIResponseDTO;
import com.example.Angler.openai.dto.OpenAIRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenAIServiceImp implements OpenAIService {

    private final WebClient webClient;
    private final String model;

    // Generator
    public OpenAIServiceImp(@Value("${spring.ai.openai.api-key}") String apiKey,
                            @Value("${spring.ai.openai.base-url}") String baseUrl,
                            @Value("${spring.ai.openai.chat.model}") String model) {
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public Mono<OpenAIResponseDTO> chat(String userMessage) {
        OpenAIRequestDTO requestDTO = new OpenAIRequestDTO(
                model,  // 설정값 그대로 사용 가능
                userMessage
        );

        return webClient.post()
                .uri("/responses")
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(OpenAIResponseDTO.class);
    }
}

