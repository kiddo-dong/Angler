package com.example.Angler.extract.service;

import com.example.Angler.extract.dto.ExtractedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExtractServiceImpl implements ExtractService {

    private static final Logger logger = LoggerFactory.getLogger(ExtractServiceImpl.class);

    @Override
    public ExtractedData parse(String extractedData) {
        logger.info("========== ExtractService 파싱 시작 ==========");
        logger.info("OpenAI 원본 응답: {}", extractedData);
        
        String url = extractValue(extractedData, "URL");
        String phone = extractValue(extractedData, "전화번호");
        String account = extractValue(extractedData, "계좌번호");

        logger.info("추출된 URL: {}", url);
        logger.info("추출된 전화번호: {}", phone);
        logger.info("추출된 계좌번호: {}", account);
        logger.info("========== ExtractService 파싱 완료 ==========");

        return new ExtractedData(url, phone, account);
    }

    private String extractValue(String text, String label) {
        // 여러 패턴 시도: "라벨: 값" 또는 "라벨 : 값" (개행 전까지)
        Pattern pattern = Pattern.compile(label + "\\s*:\\s*([^\\n\\r]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String value = matcher.group(1).trim();
            
            // 여러 종류의 빈 값 표현 처리
            if (value.isEmpty() || 
                value.matches("(?i)(null|없음|없습니다|n/a|none|-|해당없음|해당 없음|<값 또는 null>)")) {
                logger.debug("레이블 '{}': 빈 값 또는 null 표현 감지 - '{}'", label, value);
                return null;
            }
            
            // 괄호나 따옴표 제거
            value = value.replaceAll("[<>\"'()\\[\\]]", "").trim();
            
            logger.debug("레이블 '{}': 추출값='{}'", label, value);
            return value;
        }
        logger.debug("레이블 '{}': 패턴 매칭 실패", label);
        return null;
    }


}