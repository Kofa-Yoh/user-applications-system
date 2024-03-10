package com.kotkina.userapplicationssystem.exceptions;

public class RequestNotAvailableException extends RuntimeException {

    public RequestNotAvailableException() {
        super("У вас нет прав на выполнение данного запроса.");
    }

    public RequestNotAvailableException(String message) {
        super(message);
    }
}
