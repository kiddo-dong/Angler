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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AnglerServiceImp.class);

    // OpenAI Vision(gpt-4o)
    private final AiCallService aiCallService;
    // URL, 전화번호, 계좌번호 등으로 파싱/정제하는 서비스
    private final ExtractService extractService;
    // URL, 전화번호, 계좌번호 의심 여부 검사 및 서브스코어/이유 계산
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

    /**
     * 피싱 키워드 기반 점수 계산
     * @param text 분석할 텍스트
     * @return 0.0 ~ 1.0 (높을수록 의심)
     */
    private double calculateKeywordScore(String text) {
        if (text == null || text.isEmpty()) return 0.0;
        
        String[] highRiskKeywords = {
            // === 보이스피싱 - 기관 사칭 ===
            "대포통장", "명의도용", "사건연루", "개인정보유출", "안전계좌", "계좌동결",
            "녹취", "금융범죄", "지방검찰청", "수사관", "검사", "경찰관",
            "금융감독원", "금감원", "법원", "검찰", "경찰청",
            
            // === 보이스피싱 - 대출 사기 ===
            "저금리", "대출실행", "대출가능", "신용등급", "보증보험료", "기존대출상환",
            "대출승인", "한도상향", "금리인하",
            
            // === 결제/구매 관련 ===
            "Payment", "Order", "Invoice", "구매완료", "주문확인", "배송조회",
            "결제", "주문", "완료", "청구서", "송장", "결제승인", "승인번호",
            "입금", "금일", "합산", "환불", "취소", "문의전화",
            
            // === 공지/알림/긴급 ===
            "Urgent", "Notice", "긴급", "즉시", "보안경고", "로그인실패", 
            "계정차단", "본인확인", "중요공지", "안내", "알림",
            "계정정지", "카드정지", "차단됨", "잠긴", "정지",
            
            // === 이벤트/당첨 ===
            "당첨", "이벤트", "경품", "수령", "무료", "공짜", "혜택",
            "쿠폰", "할인", "이벤트당첨",
            
            // === 링크/첨부파일 유도 ===
            "클릭", "링크", "확인", "다운로드", "설치", "앱설치",
            "첨부파일", "URL",
            
            // === 시간적 압박 ===
            "지금", "바로", "즉시", "오늘", "내일까지", "시간내",
            "마감", "기한", "만료", "초과",
            
            // === 불안감 유발 ===
            "손실", "법적조치", "고소", "고발", "소송", "체포",
            "구속", "피해", "신고", "범죄", "위반",
            
            // === 금융/계좌 관련 ===
            "계좌이체", "송금", "이체", "출금", "세금", "환급",
            "국세청", "체납", "연체", "미납",
            
            // === 개인정보/인증 ===
            "비밀번호", "개인정보", "본인인증", "인증번호", "OTP",
            "보안카드", "공동인증서", "로그인", "계정",
            "보호법", "처리방침", "검토",
            
            // === 배송/택배 ===
            "택배", "배송", "보관", "미수령", "수령", "배송지연",
            
            // === 기업/서비스 사칭 ===
            "국제발신", "Microsoft", "Google", "Apple", "Amazon", 
            "Netflix", "카카오", "네이버", "은행", "카드사",
            "Meta", "Facebook", "Instagram", "WhatsApp",
            
            // === 의심 행동 ===
            "누군가", "액세스", "접속", "시도", "복구", "확인필요",
            "의심", "활동", "발견", "감지"
        };
        
        int matchCount = 0;
        
        for (String keyword : highRiskKeywords) {
            if (text.contains(keyword)) {
                // 중복 카운트 허용 (예: "결제" 2번 = 2카운트)
                int count = 0;
                int index = 0;
                while ((index = text.indexOf(keyword, index)) != -1) {
                    count++;
                    index += keyword.length();
                }
                matchCount += count;
                logger.debug("피싱 키워드 감지: '{}' ({}회)", keyword, count);
            }
        }
        
        // 키워드 1개당 0.2점, 최대 1.0
        double score = Math.min(1.0, matchCount * 0.2);
        logger.info("키워드 매칭 수: {}, 키워드 점수: {}", matchCount, score);
        
        return score;
    }

    // Image 기반 분석 Service
    @Override
    public AnglerResponseDto imagePhishingCheck(MultipartFile image) {
        String extractedData = "";
        String fullText = "";  // 전체 텍스트 (키워드 분석용)
        
        String requestText =
            "캡처된 이미지에서 모든 텍스트를 추출하고, URL, 전화번호, 계좌번호를 식별해주세요.\n" +
            "주의사항:\n" +
            "- URL: http/https로 시작하거나, .com/.net/.ms/.ly 등 도메인이 포함된 것. aka.ms, bit.ly 같은 단축 URL 포함\n" +
            "- 계좌번호: 숫자로만 이루어진 긴 숫자(10자리 이상) 또는 짧은 숫자(3~4자리)\n" +
            "- 전화번호: 010, 02 등으로 시작하거나 +로 시작하는 국제번호\n" +
            "반드시 아래 형식으로만, 다른 말 없이 답변하세요:\n" +
            "전체텍스트: <이미지의 모든 한글 텍스트>\n" +
            "URL: <값 또는 null>\n" +
            "전화번호: <값 또는 null>\n" +
            "계좌번호: <값 또는 null>\n" +
            "문자 이미지가 아니면 'false'라고만 답해주세요.";

        try{
            // 1. OpenAI API에 Image 전송
            ChatGPTResponse chatGPTResponse = aiCallService.requestImageAnalysis(image, requestText);
            String response = chatGPTResponse.getChoices().get(0).getMessage().getContent();
            logger.info("OpenAI 이미지 분석 응답: {}", response);

            if(response.equals("false")){
                logger.info("문자 이미지가 아님 - null 반환");
                return null;
            }
            
            // 전체텍스트 추출
            if (response.contains("전체텍스트:")) {
                String[] lines = response.split("\\n");
                StringBuilder fullTextBuilder = new StringBuilder();
                boolean capturing = false;
                
                for (String line : lines) {
                    if (line.startsWith("전체텍스트:")) {
                        // 전체텍스트: 다음부터 수집 시작
                        String firstPart = line.substring("전체텍스트:".length()).trim();
                        if (!firstPart.isEmpty()) {
                            fullTextBuilder.append(firstPart).append(" ");
                        }
                        capturing = true;
                    } else if (capturing) {
                        // URL:, 전화번호:, 계좌번호: 나오기 전까지 모두 수집
                        if (line.startsWith("URL:") || line.startsWith("전화번호:") || line.startsWith("계좌번호:")) {
                            break;
                        }
                        fullTextBuilder.append(line.trim()).append(" ");
                    }
                }
                
                fullText = fullTextBuilder.toString().trim();
            }
            
            // fullText가 비어있으면 response 전체를 사용
            if (fullText == null || fullText.isEmpty()) {
                fullText = response;
            }
            
            logger.info("추출된 전체텍스트: {}", fullText);
            
            extractedData = response;

        } catch (IOException e){
            logger.error("OpenAI API 이미지 분석 중 오류 발생", e);
            return new AnglerResponseDto(0.0, true, 
                List.of("이미지 분석 중 오류가 발생했습니다. 의심스러운 경우 클릭하지 마세요."));
        } catch (Exception e) {
            logger.error("예상치 못한 오류 발생", e);
            return new AnglerResponseDto(0.0, true,
                List.of("분석 중 오류가 발생했습니다. 안전을 위해 의심스러운 링크는 클릭하지 마세요."));
        }


        // 2. 데이터 정제(String -> json)
        ExtractedData refinedData = extractService.parse(extractedData);

        // 3. Validator를 통한 서브스코어 계산
        logger.info("========== Validator 점수 계산 시작 (Image) ==========");
        double domainScore = urlValidator.getScore(refinedData.getUrl());
        double phoneScore = phoneValidator.getScore(refinedData.getPhone());
        double accountScore = accountValidator.getScore(refinedData.getAccount());
        
        logger.info("도메인 점수: {}", domainScore);
        logger.info("전화번호 점수: {}", phoneScore);
        logger.info("계좌번호 점수: {}", accountScore);

        // 키워드 기반 점수 계산 (전체 텍스트 사용)
        double textKeywordScore = calculateKeywordScore(fullText);
        logger.info("키워드 점수: {}", textKeywordScore);
        
        // 계좌번호 + 전화번호 동시 존재 시 추가 가중치 (10%)
        double extra = 0.0;
        if (accountScore > 0 && phoneScore > 0) {
            extra = 0.8;  // 둘 다 있으면 매우 의심
            logger.info("계좌번호 + 전화번호 동시 존재: extra 점수 +0.8");
        }

        // 4. RiskScoreEngine으로 trustScore 계산
        logger.info("========== RiskScoreEngine 계산 시작 ==========");
        double trustScore = riskScoreEngine.computeTrustScore(
                domainScore, phoneScore, accountScore, textKeywordScore, extra
        );
        logger.info("최종 trustScore: {}", trustScore);

        boolean isPhishing = riskScoreEngine.isPhishing(trustScore);

        // 5. 이유 수집
        List<String> reasons = new ArrayList<>();
        if (domainScore > 0) reasons.add(urlValidator.getReason(refinedData.getUrl()));
        if (phoneScore > 0) reasons.add(phoneValidator.getReason(refinedData.getPhone()));
        if (accountScore > 0) reasons.add(accountValidator.getReason(refinedData.getAccount()));
        if (textKeywordScore > 0.3) reasons.add("피싱 관련 의심 키워드가 다수 포함되어 있습니다.");

        return new AnglerResponseDto(trustScore, isPhishing, reasons);
    }



    @Override
    public AnglerResponseDto textPhishingCheck(String message) {
        // 이미지에서 추출된 데이터
        String extractedData = "";

        // OpenAI의 Vision 모델에 보낼 데이터
        String requestText =
            "아래 텍스트에서 URL, 전화번호, 계좌번호를 식별해줘.\n" +
            "반드시 아래 형식으로만, 다른 말 없이 답변해. 값이 없으면 'null'이라고 써.\n" +
            "URL: <값 또는 null>\n" +
            "전화번호: <값 또는 null>\n" +
            "계좌번호: <값 또는 null>\n\n" +
            message;

        // 1. OpenAI API에 Text복사 전송
        ChatGPTResponse chatGPTResponse = aiCallService.requestTextAnalysis(requestText);
        extractedData = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        logger.info("OpenAI 텍스트 분석 응답: {}", extractedData);

        // 2. 데이터 정제
        ExtractedData refinedData = extractService.parse(extractedData);

        // 3. Validator를 통한 서브스코어 계산
        logger.info("========== Validator 점수 계산 시작 (Text) ==========");
        double domainScore = urlValidator.getScore(refinedData.getUrl());
        double phoneScore = phoneValidator.getScore(refinedData.getPhone());
        double accountScore = accountValidator.getScore(refinedData.getAccount());
        
        logger.info("도메인 점수: {}", domainScore);
        logger.info("전화번호 점수: {}", phoneScore);
        logger.info("계좌번호 점수: {}", accountScore);

        // 키워드 기반 점수 계산 (원본 메시지 + 추출 데이터 모두 분석)
        double textKeywordScore = calculateKeywordScore(message + " " + extractedData);
        double extra = 0.0;

        // 4. RiskScoreEngine으로 trustScore 계산
        logger.info("========== RiskScoreEngine 계산 시작 ==========");
        double trustScore = riskScoreEngine.computeTrustScore(
                domainScore, phoneScore, accountScore, textKeywordScore, extra
        );
        logger.info("최종 trustScore: {}", trustScore);

        boolean isPhishing = riskScoreEngine.isPhishing(trustScore);

        // 5. 이유 수집
        List<String> reasons = new ArrayList<>();
        if (domainScore > 0) reasons.add(urlValidator.getReason(refinedData.getUrl()));
        if (phoneScore > 0) reasons.add(phoneValidator.getReason(refinedData.getPhone()));
        if (accountScore > 0) reasons.add(accountValidator.getReason(refinedData.getAccount()));
        if (textKeywordScore > 0.3) reasons.add("피싱 관련 의심 키워드가 다수 포함되어 있습니다.");

        return new AnglerResponseDto(trustScore, isPhishing, reasons);
    }
}