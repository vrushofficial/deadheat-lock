package com.vrush.microservices.booking.exception;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 6769829250639411880L;

    public ValidationException() {
        super();
    }

    public ValidationException(String s) {
        super(s);
    }
}
