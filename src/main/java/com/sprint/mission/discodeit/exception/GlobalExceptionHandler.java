package com.sprint.mission.discodeit.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException e
    ) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    // favicon.ico 같은 정적 리소스가 없을 때 콘솔에 긴 에러가 찍히지 않도록 처리
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(
            NoResourceFoundException e
    ) {
        return ResponseEntity
                .notFound()
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        e.printStackTrace();

        return ResponseEntity
                .internalServerError()
                .body("서버 내부 오류가 발생했습니다. 원인: " + e.getMessage());
    }
}