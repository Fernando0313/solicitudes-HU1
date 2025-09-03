package com.first.challenge.model.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("The user with email " + email + " is already registered");
    }
}
