package com.example.Angler.Angler.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class AnglerResponseDto {
    private final double trustScore;
    private final boolean isPhishing;
    private final List<String> reasons;

    public AnglerResponseDto(double trustScore, boolean isPhishing, List<String> reasons) {
        this.trustScore = trustScore;
        this.isPhishing = isPhishing;
        this.reasons = reasons;
    }
}