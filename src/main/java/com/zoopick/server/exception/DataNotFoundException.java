package com.zoopick.server.exception;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;

@NullMarked
public class DataNotFoundException extends ZoopickException {
    private static final String CLIENT_MESSAGE_SUFFIX = "을(를) 찾을 수 없습니다.";

    public DataNotFoundException(Subject subject, String message) {
        super(HttpStatus.NOT_FOUND, subject.clientMessage, message);
    }

    @Getter
    public enum Subject {
        USER("사용자");

        public final String clientMessage;

        Subject(String clientReadable) {
            this.clientMessage = clientReadable + " " + CLIENT_MESSAGE_SUFFIX;
        }
    }
}
