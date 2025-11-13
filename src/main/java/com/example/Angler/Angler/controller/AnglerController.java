package com.example.Angler.Angler.controller;

import com.example.Angler.Angler.dto.AnglerResponseDto;
import com.example.Angler.Angler.helper.AnglerResponseFormatter;
import com.example.Angler.Angler.service.AnglerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/angler")
//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AnglerController {

    private final AnglerService anglerService;

    public AnglerController(AnglerService anglerService) {
        this.anglerService = anglerService;
    }

    // gpt gpt-4o 모델
    // image + Text 기반 OpenAI 연결 Controller
    @PostMapping("image/fishing")
    public ResponseEntity<String> imageAnalysis(@RequestParam MultipartFile image){
        // Phishing Check Service (Main Service)
        AnglerResponseDto anglerResponseDto = anglerService.imagePhishingCheck(image);

        // Response Formatting Helper Class (String Mapping)
        String finalMessage = AnglerResponseFormatter.format(anglerResponseDto);

        return ResponseEntity
                .ok(finalMessage);
    }

    // Text기반
    @PostMapping("text/fishing")
    public ResponseEntity<String> textAnalysis(@RequestBody String message){
        // Phishing Check Service (Main Service)
        AnglerResponseDto anglerResponseDto = anglerService.textPhishingCheck(message);

        // Response Formatting Helper Class (String Mapping)
        String finalMessage = AnglerResponseFormatter.format(anglerResponseDto);

        return ResponseEntity
                .ok(finalMessage);
    }
}