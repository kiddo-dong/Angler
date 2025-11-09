package com.example.Angler.extract.service;

import com.example.Angler.extract.dto.ExtractedData;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExtractServiceImpl implements ExtractService {

    @Override
    public ExtractedData parse(String extractedData) {
        String url = extractValue(extractedData, "URL");
        String phone = extractValue(extractedData, "전화번호");
        String account = extractValue(extractedData, "계좌번호");

        return new ExtractedData(url, phone, account);
    }

    private String extractValue(String text, String label) {
        Pattern pattern = Pattern.compile(label + "\\s*:\\s*(.*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String value = matcher.group(1).trim();
            value = value.split("- ")[0].trim(); // 다음 항목 전까지 잘라주기
            return value.isEmpty() ? null : value;
        }
        return null;
    }


}