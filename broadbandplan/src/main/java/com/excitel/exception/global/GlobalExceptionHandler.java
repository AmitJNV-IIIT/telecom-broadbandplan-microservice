package com.excitel.exception.global;

import com.excitel.dto.ErrorResponseDTO;
import com.excitel.exception.custom.DatabaseConnectionException;
import com.excitel.exception.custom.DuplicatePhoneNumberException;
import com.excitel.exception.custom.UserAccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorMessage("An internal server error occurred: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DatabaseConnectionException.class)
    public ResponseEntity<ErrorResponseDTO> handleDatabaseConnectionException(DatabaseConnectionException ex) {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorMessage("Error while connecting to Database "+ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UserAccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAccessDeniedException(UserAccessDeniedException ex) {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errorMessage("This route is only authorised for Admins "+ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(DuplicatePhoneNumberException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicatePhoneNumber(DuplicatePhoneNumberException ex) {

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .status(HttpStatus.CONFLICT)
                .errorMessage("New connection with duplicate phone number cannot be initiated: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
