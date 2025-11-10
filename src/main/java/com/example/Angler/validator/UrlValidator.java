package com.example.Angler.validator;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service("urlValidator")
public class UrlValidator implements ValidatorService<String> {

    @Override
    public boolean validate(String value) {
        return value != null && !value.isEmpty() && value.matches("(?i)\\b((?:https?:\\/\\/)?(?:www\\.)?[a-z0-9.-]+\\.[a-z]{2,6}(?:\\/[^\\s]*)?)\\b");
    }

    @Override
    public double getScore(String value) {
        if (!validate(value)) return 0.0;
        // 예시: 단축 URL이면 점수 높게
        if (value.matches(".*(bit\\.ly|t\\.co|tinyurl|goo\\.gl|lnkd\\.in).*")) return 0.9;
        return 0.5;
    }

    @Override
    public String getReason(String value) {
        if (!validate(value)) return "";
        if (value.matches(".*(bit\\.ly|t\\.co|tinyurl|goo\\.gl|lnkd\\.in).*")) return "짧은 URL 도메인은 피싱 가능성이 높습니다.";
        return "URL 형식 정상, 주의 필요.";
    }
}