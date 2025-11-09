package com.example.Angler.extract.dto;

import lombok.Getter;

@Getter
public class ExtractedData{
    private String url;
    private String phone;
    private String account;

    public ExtractedData(String url, String phone, String account) {
        this.url = url;
        this.phone = phone;
        this.account = account;
    }
}