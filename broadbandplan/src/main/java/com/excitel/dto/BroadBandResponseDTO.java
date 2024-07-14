package com.excitel.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class BroadBandResponseDTO {
    private HttpStatus status;//NOSONAR
    private String message;//NOSONAR
}
