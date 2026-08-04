package com.jonathan.picpay.Exceptions;

public class TypeNotSupportedForTransaction extends RuntimeException {
    public TypeNotSupportedForTransaction(String message) {
        super(message);
    }
}
