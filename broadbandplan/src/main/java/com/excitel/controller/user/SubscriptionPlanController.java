package com.excitel.controller.user;

import com.excitel.dto.ErrorResponseDTO;
import com.excitel.dto.RequestDTO;
import com.excitel.optimize.SubscriptionFeignPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * Controller class for handling user-related subscription plan operations.
 */
@RestController
@RequestMapping("/api/v2/broadband")
public class SubscriptionPlanController {

    @Autowired //NOSONAR
    private SubscriptionFeignPlanService subscriptionFeignPlanService;
/**
 * Retrieves subscription plan details based on the plan ID list and plan type.
 *
 * @param requestBody The request body containing plan ID list and plan type
 * @return ResponseEntity containing either a success response with subscription plan details or an error response
 */
    @PostMapping("/subscription-plan-detail")
    public ResponseEntity<ErrorResponseDTO.SubscriptionResponseDTO> getAllPlanByPlanIdList(@RequestBody RequestDTO.SubscriptionRequestDTO requestBody){
        // Call the subscriptionFeignPlanService to get subscription plan details

        ErrorResponseDTO.SubscriptionResponseDTO response = subscriptionFeignPlanService.getAllPlanByPlanIdList(requestBody.getPlanIdList(), requestBody.getPlanType());
        return ResponseEntity.ok(response);
    }
}
