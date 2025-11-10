package com.example.Angler.validator;

public interface ValidatorService<T> {
    boolean validate(T value);
    double getScore(T value); // 서브스코어 반환
    String getReason(T value);
}