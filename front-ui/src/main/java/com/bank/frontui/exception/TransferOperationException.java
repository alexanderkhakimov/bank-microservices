package com.bank.frontui.exception;

public class TransferOperationException extends RuntimeException {
    public TransferOperationException(String message) {
        super(message);
    }
    public TransferOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
