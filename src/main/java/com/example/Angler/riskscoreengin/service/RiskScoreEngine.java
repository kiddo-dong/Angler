package com.example.Angler.riskscoreengin.service;

import com.example.Angler.extract.dto.ExtractedData;

public interface RiskScoreEngine {
    /**
     * 정제된 데이터를 기반으로 최종 위험 점수 계산
     * @param data ExtractedData (URL, 전화번호, 계좌번호)
     * @return 0~100 점수
     */
    int calculateRiskScore(ExtractedData data);

    /**
     * 점수 기준으로 위험 레벨 메시지 반환
     * @param score 0~100 점수
     * @return 결과 메시지
     */
    String getResultMessage(int score);

    /**
     * 점수 기준으로 권장 조치 메시지 반환
     * @param score 0~100 점수
     * @return 권장 조치 메시지
     */
    String getRecommendation(int score);
}