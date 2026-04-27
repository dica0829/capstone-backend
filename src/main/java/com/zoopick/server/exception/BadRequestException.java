package com.zoopick.server.exception;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;

@NullMarked
public class BadRequestException extends ZoopickException {
    public BadRequestException(String clientMessage, String exceptionMessage) {
        super(HttpStatus.BAD_REQUEST, clientMessage, exceptionMessage);
    }

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
