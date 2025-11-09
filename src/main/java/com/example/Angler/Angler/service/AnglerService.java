package com.example.Angler.Angler.service;

import com.example.Angler.Angler.dto.AnglerResponseDto;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface AnglerService {
    // 사용자 요청받고 데이터 분석 후 응답 및 결과 리턴
    String phishingCheck(MultipartFile image);
}