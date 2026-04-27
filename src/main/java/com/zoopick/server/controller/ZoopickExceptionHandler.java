package com.zoopick.server.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.zoopick.server.dto.CommonResponse;
import com.zoopick.server.exception.ZoopickException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class ZoopickExceptionHandler {
    @ExceptionHandler(value = {FirebaseMessagingException.class, IOException.class, MessagingException.class})
    public <T> ResponseEntity<CommonResponse<T>> handleInternalServerException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error("Internal Server Error"));
    }

    @ExceptionHandler(ZoopickException.class)
    public <T> ResponseEntity<CommonResponse<T>> handleDataNotFoundException(ZoopickException exception, HttpServletRequest request) {
        log.info("({}) {}", request.getRemoteAddr(), exception.getClientMessage());
        log.debug("Detail: ", exception);
        return exception.createResponseEntity();
    }
}
