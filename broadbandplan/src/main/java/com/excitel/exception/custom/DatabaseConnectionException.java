package com.excitel.exception.custom;

public class DatabaseConnectionException extends RuntimeException{

    public DatabaseConnectionException( String message) {
        super( message);
    }
}
