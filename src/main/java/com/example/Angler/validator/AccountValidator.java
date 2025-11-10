package com.example.Angler.validator;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service("accountValidator")
public class AccountValidator implements ValidatorService<String>{
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("\\b\\d{2,3}-?\\d{2,3}-?\\d{4,5}-?\\d{2,3}\\b");

    @Override
    public boolean validate(String value) {
        return value != null && !value.isEmpty() && value.matches("\\b\\d{2,3}-?\\d{2,3}-?\\d{4,5}-?\\d{2,3}\\b");
    }

    @Override
    public double getScore(String value) {
        if (!validate(value)) return 0.0;
        // 예시: 특정 은행 계좌 범위 의심
        if (value.startsWith("123-")) return 0.9;
        return 0.5;
    }

    @Override
    public String getReason(String value) {
        if (!validate(value)) return "";
        if (value.startsWith("123-")) return "의심 계좌번호 범위에 해당합니다.";
        return "계좌번호 형식 정상, 주의 필요.";
    }
}
