package com.example.Angler.riskscoreengin.service;

import com.example.Angler.extract.dto.ExtractedData;

public class RiskScoreEngineImp implements RiskScoreEngine{
    // 가중치 초기값
    private static final int URL_WEIGHT = 40;
    private static final int PHONE_WEIGHT = 30;
    private static final int ACCOUNT_WEIGHT = 30;

    @Override
    public int calculateRiskScore(ExtractedData data) {
        int score = 0;
        if (data.getUrl() != null && !data.getUrl().isBlank()) score += URL_WEIGHT;
        if (data.getPhone() != null && !data.getPhone().isBlank()) score += PHONE_WEIGHT;
        if (data.getAccount() != null && !data.getAccount().isBlank()) score += ACCOUNT_WEIGHT;
        return Math.min(score, 100);
    }

    @Override
    public String getResultMessage(int score) {
        if (score >= 80) return "피싱 가능성이 매우 높습니다.";
        if (score >= 50) return "의심되는 요소가 있습니다.";
        return "안전한 메시지로 보입니다.";
    }

    @Override
    public String getRecommendation(int score) {
        if (score >= 80) return "즉시 신고하거나 링크를 클릭하지 마세요.";
        if (score >= 50) return "발신자 확인 후 주의하세요.";
        return "정상 메시지로 판단됩니다.";
    }
}
