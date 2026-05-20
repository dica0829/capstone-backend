package com.zoopick.server.exception;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;

@NullMarked
public class FastApiUnavailableException extends ZoopickException {

    public FastApiUnavailableException(String clientMessage, String exceptionMessage) {
        super(HttpStatus.SERVICE_UNAVAILABLE, clientMessage, exceptionMessage);
    }

    public FastApiUnavailableException(String clientMessage, String exceptionMessage, Throwable cause) {
        super(HttpStatus.SERVICE_UNAVAILABLE, clientMessage, exceptionMessage, cause);
    }
}
