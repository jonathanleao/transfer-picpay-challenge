package com.jonathan.picpay.Exceptions;

public class NoBalanceException extends RuntimeException {
    public NoBalanceException(String message) {
        super(message);
    }
}
