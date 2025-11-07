package com.example.Angler.Angler.service;

import com.example.Angler.Angler.dto.AnglerResponseDto;
import com.example.Angler.openai.service.OpenAIService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnglerServiceImp implements AnglerService{

    private final OpenAIService openAIService;

    public AnglerServiceImp(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    /*
    컨트롤러에서 받아온
    AI 모델에 이미지 또는 TEXT 넣어줌
    AI 호출 및 API(요청과 응답)
    return OpenAIResonseDto
    */
    /*
    ==========================================================================
    service에서 정제된 데이터 DTO -> 의심 패턴 판단 (피싱, 스미싱, 가짜청구 등)
    즉, 최종 응답 데이터
    return AnglerResponseDto
    */
    @Override
    public AnglerResponseDto phishingCheck(String message) {
        String messageResult = openAIService.chat(message).map(response -> response.output().get(0).content().get(0).text()).block();
        /*
        ==============================================
        받아온 데이터를 정제해주는 service로직
        "entities": {
         "urls": ["https://example.com"],
         "phones": ["010-1234-5678"],
         "accounts": ["3333-12-123456"],
         "keywords": ["환급", "보안카드"]
        } -> 정제 후 데이터 상태
        return 정제된 데이터 DTO
        */



        return new AnglerResponseDto(80, "의심문자로 보임", "신고 권장");
    }






}