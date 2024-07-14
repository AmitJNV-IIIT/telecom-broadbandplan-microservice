package com.excitel.dto;

import com.excitel.model.BroadbandConnection;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionResponseDTO {
    private HttpStatus status;//NOSONAR
    private String message;//NOSONAR
    private BroadbandConnection broadbandConnection;//NOSONAR
}