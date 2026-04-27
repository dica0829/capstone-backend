package com.zoopick.server.exception;

import com.zoopick.server.dto.CommonResponse;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Getter
public class ZoopickException extends RuntimeException {
    private final HttpStatusCode statusCode;
    private final String clientMessage;

    protected ZoopickException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.clientMessage = message;
    }

    protected ZoopickException(HttpStatusCode statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.clientMessage = message;
    }

    protected ZoopickException(HttpStatusCode statusCode, String clientMessage, String exceptionMessage) {
        super(exceptionMessage);
        this.statusCode = statusCode;
        this.clientMessage = clientMessage;
    }

    protected ZoopickException(HttpStatusCode statusCode, String clientMessage, String exceptionMessage, Throwable cause) {
        super(exceptionMessage, cause);
        this.statusCode = statusCode;
        this.clientMessage = clientMessage;
    }

    public <T> ResponseEntity<CommonResponse<T>> createResponseEntity() {
        return ResponseEntity.status(statusCode).body(CommonResponse.error(clientMessage));
    }
}
