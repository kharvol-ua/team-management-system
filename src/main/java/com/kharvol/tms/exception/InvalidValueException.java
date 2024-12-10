package com.kharvol.tms.exception;

public class InvalidValueException extends RuntimeException {

    public InvalidValueException() {
        super();
    }

    public InvalidValueException(String message) {
        super(message);
    }

    public InvalidValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
