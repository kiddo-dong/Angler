package com.example.Angler.validator.account;

import com.example.Angler.validator.ValidatorService;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service("accountValidator")
public class AccountValidator implements ValidatorService<String> {
    // 다양한 계좌번호 형식 지원: 123-456-789, 20180809-001112, 3789 등
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile(
        "(\\b\\d{2,3}-?\\d{2,3}-?\\d{4,6}-?\\d{2,3}\\b|" +  // 일반 계좌: 123-456-78901
        "\\b\\d{8,12}-\\d{4,8}\\b|" +                      // 긴 형식: 20180809-001112
        "\\b\\d{11,16}\\b|" +                               // 하이픈 없는 긴 계좌
        "\\b\\d{4}\\b)");                                   // 짧은 계좌번호: 3789

    @Override
    public boolean validate(String value) {
        if (value == null || value.isEmpty()) return false;
        return ACCOUNT_PATTERN.matcher(value).find();
    }

    @Override
    public double getScore(String value) {
        if (!validate(value)) return 0.0;
        // 특정 의심 계좌 패턴
        if (value.startsWith("123-") || value.startsWith("000-")) return 0.9;
        // 기본적으로 문자에 계좌번호가 있으면 의심 (더 보수적)
        return 0.7;
    }

    @Override
    public String getReason(String value) {
        if (!validate(value)) return "";
        if (value.startsWith("123-") || value.startsWith("000-")) {
            return "의심스러운 계좌번호 패턴이 감지되었습니다.";
        }
        return "문자에 계좌번호가 포함되어 있어 피싱 가능성이 있습니다. 절대 송금하지 마세요.";
    }
}
