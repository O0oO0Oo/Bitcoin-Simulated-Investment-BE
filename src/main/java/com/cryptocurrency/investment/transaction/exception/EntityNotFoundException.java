package com.cryptocurrency.investment.transaction.exception;

public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1;

    private static final String mssage = " Not Found.";
    public EntityNotFoundException(String entityName) {
        super(entityName + mssage);
    }
}
