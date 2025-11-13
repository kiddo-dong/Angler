package com.example.Angler.validator.url;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class GoogleSafeBrowsingService {

    private final WebClient webClient;
    private final String apiKey;
    private final String endpoint;

    public GoogleSafeBrowsingService(
            @Value("${google.safebrowsing.api-key}") String apiKey,
            @Value("${google.safebrowsing.endpoint}") String endpoint
    ) {
        this.apiKey = apiKey;
        this.endpoint = endpoint;
        this.webClient = WebClient.create();
    }

    public Mono<Boolean> isUrlSafe(String url) {
        Map<String, Object> body = Map.of(
                "client", Map.of(
                        "clientId", "angler-app",
                        "clientVersion", "1.0"
                ),
                "threatInfo", Map.of(
                        "threatTypes", List.of("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE"),
                        "platformTypes", List.of("ANY_PLATFORM"),
                        "threatEntryTypes", List.of("URL"),
                        "threatEntries", List.of(Map.of("url", url))
                )
        );

        return webClient.post()
                .uri(endpoint + "?key=" + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> !response.containsKey("matches"))  // matches 있으면 위험
                .onErrorReturn(false);
    }
}

