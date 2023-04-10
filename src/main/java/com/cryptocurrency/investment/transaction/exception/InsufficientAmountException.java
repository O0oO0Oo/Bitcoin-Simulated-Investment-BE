package com.cryptocurrency.investment.transaction.exception;

public class InsufficientAmountException extends RuntimeException {
    private static final long serialVersionUID = 3;

    public InsufficientAmountException(String message) {
        super(message);
    }
}
