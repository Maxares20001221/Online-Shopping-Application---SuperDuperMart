package com.example.superdupermart.exception;

/**
 * 登录凭证不正确时抛出的业务异常
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Incorrect credentials, please try again.");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}


