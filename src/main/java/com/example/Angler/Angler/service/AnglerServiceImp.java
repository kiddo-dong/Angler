package com.example.Angler.Angler.service;

import com.example.Angler.Angler.dto.AnglerResponseDto;
import com.example.Angler.extract.dto.ExtractedData;
import com.example.Angler.extract.service.ExtractService;
import com.example.Angler.openai.vision.response.ChatGPTResponse;
import com.example.Angler.openai.vision.service.AiCallService;
import com.example.Angler.riskscoreengin.service.RiskScoreEngine;
import com.example.Angler.validator.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AnglerServiceImp implements AnglerService{

    // OpenAI Vision(gpt-4o)
    private final AiCallService aiCallService;
    // URL, 전화번호, 계좌번호 등으로 파싱/정제하는 서비스
    private final ExtractService extractService;
    // URL 의심 여부 검사 및 서브스코어/이유 계산
    private final ValidatorService<String> urlValidator;
    private final ValidatorService<String> phoneValidator;
    private final ValidatorService<String> accountValidator;
    // 각 서브스코어를 가중합으로 합쳐 최종 위험 점수 계산
    private final RiskScoreEngine riskScoreEngine;

    // Generator
    @Autowired
    public AnglerServiceImp(
            AiCallService aiCallService,
            ExtractService extractService,
            @Qualifier("urlValidator") ValidatorService<String> urlValidator,
            @Qualifier("phoneValidator") ValidatorService<String> phoneValidator,
            @Qualifier("accountValidator") ValidatorService<String> accountValidator,
            RiskScoreEngine riskScoreEngine
    ) {
        this.aiCallService = aiCallService;
        this.extractService = extractService;
        this.urlValidator = urlValidator;
        this.phoneValidator = phoneValidator;
        this.accountValidator = accountValidator;
        this.riskScoreEngine = riskScoreEngine;
    }


    // FE(image | File 형식) -> Text(GPT Vison 모델에서 추출) -> Text 데이터 정제 -> 정제된 데이터 스코어링
    @Override
    public AnglerResponseDto phishingCheck(MultipartFile image) {
        // 이미지에서 추출된 데이터
        String extractedData = "";

        // OpenAI의 Vision 모델에 보낼 데이터
        String requestText =
                "캡쳐된 이미지에서 URL, 전화번호, 계좌번호를 식별해줘" +
                "만약에 URL 또는 전화번호 또는 계좌번호 중에 존재하지 않는것은 응답하지마.";


        try{
            ChatGPTResponse chatGPTResponse = aiCallService.requestImageAnalysis(image, requestText);
            extractedData = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        } catch (IOException e){
            // TODO Logging 필요
        }

        // 2. 데이터 정제
        ExtractedData refinedData = extractService.parse(extractedData);

        // 3. Validator를 통한 서브스코어 계산
        double domainScore = urlValidator.getScore(refinedData.getUrl());
        double phoneScore = phoneValidator.getScore(refinedData.getPhone());
        double accountScore = accountValidator.getScore(refinedData.getAccount());

        // TODO: textKeywordScore, extra 점수는 필요 시 추가
        double textKeywordScore = 0.0;
        double extra = 0.0;

        // 4. RiskScoreEngine으로 trustScore 계산
        double trustScore = riskScoreEngine.computeTrustScore(
                domainScore, phoneScore, accountScore, textKeywordScore, extra
        );

        boolean isPhishing = riskScoreEngine.isPhishing(trustScore);

        // 5. 이유 수집
        List<String> reasons = new ArrayList<>();
        if (domainScore > 0) reasons.add(urlValidator.getReason(refinedData.getUrl()));
        if (phoneScore > 0) reasons.add(phoneValidator.getReason(refinedData.getPhone()));
        if (accountScore > 0) reasons.add(accountValidator.getReason(refinedData.getAccount()));

        return new AnglerResponseDto(trustScore, isPhishing, reasons);
    }
}