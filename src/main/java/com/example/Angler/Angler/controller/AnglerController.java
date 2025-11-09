package com.example.Angler.Angler.controller;

import com.example.Angler.Angler.service.AnglerService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/angler")
public class AnglerController {

    private final AnglerService anglerService;

    public AnglerController(AnglerService anglerService) {
        this.anglerService = anglerService;
    }


    // gpt gpt-4o 모델
    // image + Text 기반 OpenAI 연결 Controller
    @PostMapping("/fishing")
    public String imageAnalysis(@RequestParam MultipartFile image){
        return anglerService.phishingCheck(image);
    }
}