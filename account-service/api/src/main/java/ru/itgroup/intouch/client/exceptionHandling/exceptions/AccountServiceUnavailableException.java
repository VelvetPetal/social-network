package ru.itgroup.intouch.client.exceptionHandling.exceptions;

public class AccountServiceUnavailableException extends RuntimeException{
    public AccountServiceUnavailableException(String message) {
        super(message);
    }
}
