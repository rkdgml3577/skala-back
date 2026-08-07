package com.sk.skala.shopapi.exception;

import com.sk.skala.shopapi.data.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice   // 모든 컨트롤러의 예외를 여기서 처리
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseException.class)
    public ResponseEntity<Response> handleResponseException(ResponseException e) {
        Response response = new Response();
        response.setResult(Response.ERROR);
        response.setMessage(e.getMessage());
        response.setBody(e.getError().name());  // 에러 코드 (DATA_NOT_FOUND 등)

        // 에러 종류에 따라 HTTP 상태코드 결정
        HttpStatus status = switch (e.getError()) {
            case DATA_NOT_FOUND -> HttpStatus.NOT_FOUND;           // 404
            case DATA_DUPLICATED -> HttpStatus.CONFLICT;           // 409
            case NOT_AUTHENTICATED -> HttpStatus.UNAUTHORIZED;     // 401
            case INSUFFICIENT_FUNDS -> HttpStatus.BAD_REQUEST;     // 400
        };
        return ResponseEntity.status(status).body(response);
    }

    // 그 외 모든 예외 (예상 못한 에러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e) {
        Response response = new Response();
        response.setResult(Response.ERROR);
        response.setMessage("서버 오류: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}