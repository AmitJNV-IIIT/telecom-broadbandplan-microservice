package com.excitel.dto;


import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@Builder
public class ResponseDTO {
    private HttpStatus status; //NOSONAR
    private String message; //NOSONAR
    private String mobileNumber; //NOSONAR
    private String email; //NOSONAR
    private String role; //NOSONAR

    public ResponseDTO(HttpStatus statusCode, String errorMessage, String phoneNumber, String email, String role) {
        this.status = statusCode;
        this.message = errorMessage;
        this.mobileNumber = phoneNumber;
        this.email = email;
        this.role = role;
    }

}