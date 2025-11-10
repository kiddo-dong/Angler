package com.example.Angler.validator;

import org.springframework.stereotype.Service;

@Service("phoneValidator")
public class PhoneValitor implements ValidatorService<String>{

    @Override
    public boolean validate(String value) {
        return value != null && !value.isEmpty() && value.matches("\\b(01[016789]-?\\d{3,4}-?\\d{4})\\b");
    }

    @Override
    public double getScore(String value) {
        if (!validate(value)) return 0.0;
        // 예시: 스팸 신고된 번호라면 높게
        if (value.startsWith("010-123")) return 0.9;
        return 0.5;
    }

    @Override
    public String getReason(String value) {
        if (!validate(value)) return "";
        if (value.startsWith("010-123")) return "과거 스팸 신고된 번호와 유사합니다.";
        return "전화번호 형식 정상, 주의 필요.";
    }
}
