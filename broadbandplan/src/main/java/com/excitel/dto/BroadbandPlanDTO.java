package com.excitel.dto;

import com.excitel.model.BroadbandPlan;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BroadbandPlanDTO {
    private HttpStatus status;//NOSONAR
    private BroadbandPlan data;//NOSONAR
}