package com.zoopick.server.exception;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;

@NullMarked
public class AccessTokenException extends ZoopickException {
    private static final String CLIENT_MESSAGE = "토큰이 유효하지 않습니다.";

    public AccessTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, CLIENT_MESSAGE, message);
    }

    public AccessTokenException(String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, CLIENT_MESSAGE, message, cause);
    }
}
