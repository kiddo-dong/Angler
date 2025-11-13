package com.example.Angler.Angler.service;
import com.example.Angler.Angler.dto.AnglerResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface AnglerService {
    // image 기반
    AnglerResponseDto imagePhishingCheck(MultipartFile image);
}