package com.excitel.exception.custom;

public class UserAccessDeniedException extends RuntimeException{
    public UserAccessDeniedException(String message) {
        super(message);
    }
}
