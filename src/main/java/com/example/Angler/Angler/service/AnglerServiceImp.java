package com.example.Angler.Angler.service;

import com.example.Angler.Angler.dto.AnglerResponseDto;
import com.example.Angler.openai.vision.response.ChatGPTResponse;
import com.example.Angler.openai.vision.service.AiCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class AnglerServiceImp implements AnglerService{

    // OpenAI Vision(gpt-4o)
    private final AiCallService aiCallService;

    @Autowired
    public AnglerServiceImp(AiCallService aiCallService) {
        this.aiCallService = aiCallService;
    }

    // 데이터 흐름
    // FE(image | File 형식) -> Text(GPT Vison 모델에서 추출) -> Text 데이터 정제 -> 정제된 데이터 스코어링
    @Override
    public String phishingCheck(MultipartFile image) {
        // 이미지에서 추출된 데이터
        String dataExtraction;

        // OpenAI의 Vision 모델에 보낼 데이터
        String requestText =
                "캡쳐된 이미지에서 URL, 전화번호, 계좌번호를 식별해줘" +
                "만약에 URL 또는 전화번호 또는 계좌번호 중에 존재하지 않는것은 응답하지마.";
        /*
        OpenAI API로 요청 및 응답(이미지 데이터 -> 텍스트 데이터)
        */
        try{
            ChatGPTResponse chatGPTResponse = aiCallService.requestImageAnalysis(image, requestText);
            dataExtraction = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        } catch (IOException e){
            return "잘못된 데이터 입력";
        }
        return dataExtraction;


        /*
        ==============================================

        } -> 정제 후 데이터 상태
       */


        /*
        ==========================================================================
        service에서 정제된 데이터 DTO -> 의심 패턴 판단 (피싱, 스미싱, 가짜청구 등)
        즉, 최종 응답 데이터
        return AnglerResponseDto
        */
    }
}