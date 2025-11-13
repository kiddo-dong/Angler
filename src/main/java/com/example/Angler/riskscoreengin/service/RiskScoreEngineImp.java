package com.example.Angler.riskscoreengin.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RiskScoreEngineImp implements RiskScoreEngine {

    private static final Logger logger = LoggerFactory.getLogger(RiskScoreEngineImp.class);

    /**
     * trustScore 계산
     * @param domainScore URL 의심 정도 (0~1)
     * @param phoneScore 전화번호 의심 정도 (0~1)
     * @param accountScore 계좌번호 의심 정도 (0~1)
     * @param textKeywordScore 키워드 기반 의심도 (0~1)
     * @param extra 기타 점수 (0~1)
     * @return trustScore 0~100 (높을수록 안전)
     */
    @Override
    public double computeTrustScore(double domainScore, double phoneScore, double accountScore,
                                    double textKeywordScore, double extra) {
        logger.info("입력값 - domain:{}, phone:{}, account:{}, keyword:{}, extra:{}", 
                    domainScore, phoneScore, accountScore, textKeywordScore, extra);
        
        double riskScore = domainScore * 0.4
                + textKeywordScore * 0.3
                + (phoneScore + accountScore) * 0.2 / 2
                + extra * 0.1;

        logger.info("계산된 riskScore: {}", riskScore);
        
        double trustScore = (1 - riskScore) * 100;
        logger.info("변환 전 trustScore: {}", trustScore);

        // 0~100 범위 보정
        if(trustScore > 100) trustScore = 100;
        if(trustScore < 0) trustScore = 0;

        logger.info("최종 trustScore (보정 후): {}", trustScore);
        return trustScore;
    }

    /**
     * 피싱 여부 판단
     * trustScore가 낮으면 위험, 높으면 안전
     */
    @Override
    public boolean isPhishing(double trustScore) {
        if (trustScore < 50) {
            return true;  // 위험 (50점 미만)
        } else if (trustScore < 80) {
            return false; // 주의 (50~79점)
        } else {
            return false; // 안전 (80점 이상)
        }
    }
}