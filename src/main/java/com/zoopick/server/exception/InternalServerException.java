package com.zoopick.server.exception;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;

@NullMarked
public class InternalServerException extends ZoopickException {
    private static final String CLIENT_MESSAGE = "서버에 문제가 발생했습니다.";

    public InternalServerException(String exceptionMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, CLIENT_MESSAGE, exceptionMessage);
    }

    public InternalServerException(String exceptionMessage, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, CLIENT_MESSAGE, exceptionMessage, cause);
    }
}
