package com.kotkina.userapplicationssystem.exceptions;

public class CurrentUserNotDefinedException extends RuntimeException {

    public CurrentUserNotDefinedException() {
        super("Текущий пользователь не определен. Выполните вход.");
    }

    public CurrentUserNotDefinedException(String message) {
        super(message);
    }
}
