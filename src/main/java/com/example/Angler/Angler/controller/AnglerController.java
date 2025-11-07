package com.example.Angler.Angler.controller;


import com.example.Angler.Angler.dto.AnglerResponseDto;
import com.example.Angler.Angler.service.AnglerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/angler")
public class AnglerController {

    private final AnglerService anglerService;

    public AnglerController(AnglerService anglerService) {
        this.anglerService = anglerService;
    }


    @PostMapping("/fishing")
    public ResponseEntity<AnglerResponseDto> PhishingCheck(@RequestBody String message){
        /*
         AI 모델에 이미지 또는 TEXT 넣어줌
         AI 호출 및 API(요청과 응답)
        */
        /*
        service에서 정제된 데이터 DTO -> 의심 패턴 판단 (피싱, 스미싱, 가짜청구 등)
        즉, 최종 응답 데이터
        return AnglerResponseDto
        */
        return ResponseEntity
                .ok(anglerService.phishingCheck(message));
    }


}