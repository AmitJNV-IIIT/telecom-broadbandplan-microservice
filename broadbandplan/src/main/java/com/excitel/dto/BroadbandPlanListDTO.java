package com.excitel.dto;

import com.excitel.model.BroadbandPlan;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BroadbandPlanListDTO {
    private HttpStatus status;//NOSONAR
    private List<BroadbandPlan> data;//NOSONAR
}
