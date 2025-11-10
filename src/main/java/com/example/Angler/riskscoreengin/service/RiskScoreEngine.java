package com.example.Angler.riskscoreengin.service;

import com.example.Angler.extract.dto.ExtractedData;

public interface RiskScoreEngine {
    public double computeTrustScore(double domainScore, double phoneScore, double accountScore, double textKeywordScore, double extra);
    public boolean isPhishing(double trustScore);
}