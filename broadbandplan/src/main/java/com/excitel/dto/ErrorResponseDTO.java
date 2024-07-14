package com.excitel.dto;

import com.excitel.model.BroadbandPlan;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {
    private HttpStatus status; //NOSONAR
    private String errorMessage; //NOSONAR

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class SubscriptionResponseDTO {
        private HttpStatus status; //NOSONAR
        private Map<String, BroadbandPlan> mobilePlans; //NOSONAR
    }
}
