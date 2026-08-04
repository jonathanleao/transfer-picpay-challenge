package com.jonathan.picpay.Exceptions;

public class SelfTransactionException extends RuntimeException {
    public SelfTransactionException(String message) {
        super(message);
    }
}
