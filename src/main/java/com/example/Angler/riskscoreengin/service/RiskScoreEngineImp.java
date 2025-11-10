package com.example.Angler.riskscoreengin.service;


import org.springframework.stereotype.Service;

@Service
public class RiskScoreEngineImp implements RiskScoreEngine{

    /**
     * trustScore 계산
     * @param domainScore URL 의심 정도 (0~1)
     * @param phoneScore 전화번호 의심 정도 (0~1)
     * @param accountScore 계좌번호 의심 정도 (0~1)
     * @param textKeywordScore 키워드 기반 의심도 (0~1)
     * @param extra 기타 점수 (0~1)
     * @return trustScore 0~100
     */
    @Override
    public double computeTrustScore(double domainScore, double phoneScore, double accountScore,
                                    double textKeywordScore, double extra) {
        double riskScore = domainScore * 0.35
                + textKeywordScore * 0.30
                + (phoneScore + accountScore) * 0.30 / 2
                + extra * 0.05;
        return (1 - riskScore) * 100;
    }

    @Override
    public boolean isPhishing(double trustScore) {
        if (trustScore >= 75) {
            // 높은 점수 → 위험함
            return true;
        } else if (trustScore >= 40) {
            // 중간 점수 → 주의 수준, 필요 시 false 처리
            return false;
        } else {
            // 낮은 점수 → 안전
            return false;
        }
    }
}