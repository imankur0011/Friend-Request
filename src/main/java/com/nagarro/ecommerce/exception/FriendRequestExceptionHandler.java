package com.nagarro.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FriendRequestExceptionHandler {

    @ExceptionHandler(FriendRequestException.class)
    public ResponseEntity<String> handleFriendRequestException(FriendRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Add more exception handlers for other exceptions if needed
}
