package com.micro.job.exception;

import java.io.Serial;

public class DublicateResourceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public DublicateResourceException(String message) {
        super(message);
    }
}
