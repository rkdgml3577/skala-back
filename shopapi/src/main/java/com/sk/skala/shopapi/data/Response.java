package com.sk.skala.shopapi.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private String result;   // SUCCESS or ERROR
    private String message;
    private Object body;     // 실제 데이터

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    public void setSuccess(Object body) {
        this.result = SUCCESS;
        this.message = "OK";
        this.body = body;
    }
}