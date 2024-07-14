package com.excitel.exception.custom;

public class DuplicatePhoneNumberException extends RuntimeException{
    public DuplicatePhoneNumberException(String message) {
        super(message);
    }
}
