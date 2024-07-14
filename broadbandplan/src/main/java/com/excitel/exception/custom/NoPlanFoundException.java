package com.excitel.exception.custom;

public class NoPlanFoundException extends RuntimeException{
    public NoPlanFoundException(String message) {
        super(message);
    }
}
