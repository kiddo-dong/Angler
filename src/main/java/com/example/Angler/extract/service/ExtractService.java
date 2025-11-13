package com.example.Angler.extract.service;

import com.example.Angler.extract.dto.ExtractedData;

public interface ExtractService {
    ExtractedData parse(String extractedData);
}