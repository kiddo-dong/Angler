package com.example.Angler.openai.controller;

import com.example.Angler.openai.service.OpenAIService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("open-ai")
public class OpenAIController {
    private final OpenAIService openAIService;

    // DI
    public OpenAIController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/chat")
    public Mono<String> chat(@RequestBody String message) {
        return openAIService.chat(message)
                .map(response -> response.output().get(0).content().get(0).text());
    }
}