package com.example.Angler.Angler.service;

import com.example.Angler.extract.dto.ExtractedData;
import com.example.Angler.extract.service.ExtractService;
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
    private final ExtractService extractService;

    @Autowired
    public AnglerServiceImp(AiCallService aiCallService,ExtractService extractService) {
        this.aiCallService = aiCallService;
        this.extractService = extractService;
    }

    // 데이터 흐름
    // FE(image | File 형식) -> Text(GPT Vison 모델에서 추출) -> Text 데이터 정제 -> 정제된 데이터 스코어링
    @Override
    public String phishingCheck(MultipartFile image) {
        // 이미지에서 추출된 데이터
        String extractedData;

        // OpenAI의 Vision 모델에 보낼 데이터
        String requestText =
                "캡쳐된 이미지에서 URL, 전화번호, 계좌번호를 식별해줘" +
                "만약에 URL 또는 전화번호 또는 계좌번호 중에 존재하지 않는것은 응답하지마.";
        /*
        OpenAI API로 요청 및 응답(이미지 데이터 -> 텍스트 데이터)
        */
        try{
            ChatGPTResponse chatGPTResponse = aiCallService.requestImageAnalysis(image, requestText);
            extractedData = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        } catch (IOException e){
            return "잘못된 데이터 입력";
        }

        // 텍스트 처리된 데이터 정제 -> Object
        ExtractedData refinedData = extractService.parse(extractedData);
        String result = "정제된 데이터 | URL : " + refinedData.getUrl() + " | 전화번호 : " + refinedData.getPhone() + " | 계좌번호 : " + refinedData.getAccount();
        /*
        ==========================================================================
        의심 패턴 판단 로직
        */
        return result;
    }
}