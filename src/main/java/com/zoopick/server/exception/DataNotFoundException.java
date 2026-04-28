package com.zoopick.server.exception;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;

@NullMarked
public class DataNotFoundException extends ZoopickException {
    private static final String CLIENT_MESSAGE_SUFFIX = "을(를) 찾을 수 없습니다.";

    public static DataNotFoundException from(String resourceName, Object target) {
        return new DataNotFoundException(
                resourceName + CLIENT_MESSAGE_SUFFIX,
                String.format("%s not found: %s", resourceName, target)
        );
    }

    public DataNotFoundException(String clientMessage, String exceptionMessage) {
        super(HttpStatus.NOT_FOUND, clientMessage, exceptionMessage);
    }
}
