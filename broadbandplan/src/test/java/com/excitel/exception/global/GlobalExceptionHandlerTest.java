package com.excitel.exception.global;

import com.excitel.dto.ErrorResponseDTO;
import com.excitel.exception.custom.DatabaseConnectionException;
import com.excitel.exception.custom.DuplicatePhoneNumberException;
import com.excitel.exception.custom.UserAccessDeniedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleException_ReturnsInternalServerError() {
        // Arrange
        Exception ex = new Exception("Internal server error");

        // Act
        ResponseEntity        <ErrorResponseDTO> responseEntity = globalExceptionHandler.handleException(ex);
        ErrorResponseDTO responseBody = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseBody.getStatus());
        assertEquals("An internal server error occurred: Internal server error", responseBody.getErrorMessage());
    }

    @Test
    void handleDatabaseConnectionException_ReturnsInternalServerError() {
        // Arrange
        DatabaseConnectionException ex = new DatabaseConnectionException("Connection failed");

        // Act
        ResponseEntity        <ErrorResponseDTO> responseEntity = globalExceptionHandler.handleDatabaseConnectionException(ex);
        ErrorResponseDTO responseBody = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseBody.getStatus());
        assertEquals("Error while connecting to Database Connection failed", responseBody.getErrorMessage());
    }

    @Test
    void handleUserAccessDeniedException_ReturnsUnauthorized() {
        // Arrange
        UserAccessDeniedException ex = new UserAccessDeniedException("Access denied");

        // Act
        ResponseEntity        <ErrorResponseDTO> responseEntity = globalExceptionHandler.handleUserAccessDeniedException(ex);
        ErrorResponseDTO responseBody = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, responseBody.getStatus());
        assertEquals("This route is only authorised for Admins Access denied", responseBody.getErrorMessage());
    }

    @Test
    void handleDuplicatePhoneNumber_ReturnsConflict() {
        // Arrange
        DuplicatePhoneNumberException ex = new DuplicatePhoneNumberException("Duplicate phone number");

        // Act
        ResponseEntity        <ErrorResponseDTO> responseEntity = globalExceptionHandler.handleDuplicatePhoneNumber(ex);
        ErrorResponseDTO responseBody = responseEntity.getBody();

        // Assert
        assertEquals(HttpStatus.CONFLICT, responseBody.getStatus());
        assertEquals("New connection with duplicate phone number cannot be initiated: Duplicate phone number", responseBody.getErrorMessage());
    }
}




