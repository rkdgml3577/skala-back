package com.sk.skala.shopapi.exception;

public enum Error {
    DATA_NOT_FOUND("데이터를 찾을 수 없습니다"),
    DATA_DUPLICATED("이미 존재하는 데이터입니다"),
    INSUFFICIENT_FUNDS("포인트가 부족합니다"),
    NOT_AUTHENTICATED("인증에 실패했습니다");

    private final String message;
    Error(String message) { this.message = message; }
    public String getMessage() { return message; }
}