package com.sahibinden.codecase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadStatusTransition extends RuntimeException {
    public BadStatusTransition(String message) {
        super(message);
    }
}
