package com.example.Angler.validator.phone;

import com.example.Angler.validator.ValidatorService;
import org.springframework.stereotype.Service;

@Service("phoneValidator")
public class PhoneValitor implements ValidatorService<String> {

    @Override
    public boolean validate(String value) {
        if (value == null || value.isEmpty()) return false;
        // 한국 휴대폰: 010-1234-5678, 01012345678
        boolean mobileFormat = value.matches(".*(01[016789])-?\\d{3,4}-?\\d{4}.*");
        // 한국 일반 전화: 02-1234-5678, 0212345678, 031-123-4567
        boolean landlineFormat = value.matches(".*(0\\d{1,2})-?\\d{3,4}-?\\d{4}.*");
        // 국제 전화번호: +46 76-943 96 07
        boolean intlFormat = value.matches(".*\\+\\d{1,3}[\\s-]?\\d{1,4}[\\s-]?\\d{1,4}[\\s-]?\\d{1,4}[\\s-]?\\d{0,4}.*");
        return mobileFormat || landlineFormat || intlFormat;
    }

    @Override
    public double getScore(String value) {
        if (!validate(value)) return 0.0;
        // 특정 스팸 패턴 고위험
        if (value.startsWith("010-123") || value.startsWith("010-000")) return 0.9;
        // 기본적으로 문자에 전화번호가 있으면 의심 (더 보수적)
        return 0.7;
    }

    @Override
    public String getReason(String value) {
        if (!validate(value)) return "";
        if (value.startsWith("010-123") || value.startsWith("010-000")) {
            return "과거 스팸 신고된 번호 패턴과 유사합니다.";
        }
        return "문자에 전화번호가 포함되어 있어 피싱 가능성이 있습니다. 신중히 확인하세요.";
    }
}
