package com.cryptocurrency.investment.transaction.exception;

public class InsufficientFundException extends RuntimeException {
    private static final long serialVersionUID = 2;

    public InsufficientFundException(String message) {
        super(message);
    }
}
