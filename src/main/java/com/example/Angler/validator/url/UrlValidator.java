package com.example.Angler.validator.url;

import com.example.Angler.validator.ValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("urlValidator")
public class UrlValidator implements ValidatorService<String> {

    private static final Logger logger = LoggerFactory.getLogger(UrlValidator.class);

    private final GoogleSafeBrowsingService safeBrowsingService;

    public UrlValidator(GoogleSafeBrowsingService safeBrowsingService) {
        this.safeBrowsingService = safeBrowsingService;
    }

    @Override
    public boolean validate(String value) {
        // 형식(패턴) 검증만 수행: 존재 여부와 URL 형식만 확인
        if (value == null || value.isEmpty()) return false;
        return value.matches("(?i)\\b((?:https?:\\/\\/)?(?:www\\.)?[a-z0-9.-]+\\.[a-z]{2,6}(?:\\/[^\\s]*)?)\\b");
    }

    @Override
    public double getScore(String value) {
        // 값이 없거나 형식이 아니면 URL 기반 리스크 없음으로 간주
        if (!validate(value)) return 0.0;

        double score;
        boolean isShortUrl = value.matches(".*(bit\\.ly|t\\.co|tinyurl|goo\\.gl|lnkd\\.in|aka\\.ms|ow\\.ly|tiny\\.cc|rb\\.gy|fb\\.me|t\\.me|cutt\\.ly|short\\.link|is\\.gd).*");
        logger.info("URL 분석 시작: {}, 단축URL 여부: {}", value, isShortUrl);
        
        try {
            // 1. 모든 URL을 Safe Browsing API로 검사
            boolean safe = safeBrowsingService.isUrlSafe(value).block();
            logger.info("Safe Browsing API 결과: safe={}", safe);
            
            // 2. 결과에 따라 점수 부여
            if (!safe) {
                // 위험 URL로 판별됨
                score = 1.0;
                logger.info("위험 URL 판정 → 점수: {}", score);
            } else if (isShortUrl) {
                // Safe Browsing은 안전하다고 했지만 단축 URL이므로 의심
                score = 0.7;
                logger.info("단축 URL (Safe Browsing 안전 판정) → 점수: {}", score);
            } else {
                // 안전한 일반 URL
                score = 0.0;
                logger.info("안전한 일반 URL → 점수: {}", score);
            }
        } catch (Exception e) {
            // 3. API 호출 실패 시 단축 URL 여부로만 판단
            logger.warn("Safe Browsing API 호출 실패 → 단축URL 여부로 판단: {}", e.getMessage());
            score = isShortUrl ? 0.8 : 0.5;
        }
        
        logger.info("URL: {}, 최종 점수: {}", value, score);
        return score;
    }

    @Override
    public String getReason(String value) {
        if (!validate(value)) return "";

        String reason;
        if (value.matches(".*(bit\\.ly|t\\.co|tinyurl|goo\\.gl|lnkd\\.in|aka\\.ms|ow\\.ly|tiny\\.cc|rb\\.gy|fb\\.me|t\\.me|cutt\\.ly|short\\.link|is\\.gd).*")) {
            reason = "짧은 URL 도메인은 피싱 가능성이 높습니다.";
        } else {
            reason = "URL 형식 정상, 주의 필요.";
        }

        try {
            boolean safe = safeBrowsingService.isUrlSafe(value).block();
            if (!safe) reason += " Google Safe Browsing 검사 결과 위험 URL로 판별됨.";
            logger.info("URL: {}, Reason: {}", value, reason);
        } catch (Exception e) {
            logger.warn("Safe Browsing API 호출 실패로 Reason 제한: URL={}, 예외={}", value, e.toString());
        }

        return reason;
    }
}