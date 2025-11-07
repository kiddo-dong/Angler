package com.example.Angler.openai.dto;

import java.util.List;

public record OpenAIResponseDTO(List<Output> output) {
    public record Output(List<Content> content) {
        public record Content(String type, String text) {}
    }
}
