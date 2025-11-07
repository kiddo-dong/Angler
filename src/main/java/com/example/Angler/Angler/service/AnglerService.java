package com.example.Angler.Angler.service;

import com.example.Angler.Angler.dto.AnglerResponseDto;

public interface AnglerService {
    // 사용자 요청/응답 전달용
    AnglerResponseDto phishingCheck(String message);
}