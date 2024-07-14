package com.excitel.controller.user;

import com.excitel.dto.ErrorResponseDTO;
import com.excitel.dto.RequestDTO;
import com.excitel.dto.ResponseDTO;
import com.excitel.optimize.SubscriptionFeignPlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class SubscriptionPlanControllerTest {

    @Mock
    private SubscriptionFeignPlanService subscriptionFeignPlanService;

    @InjectMocks
    private SubscriptionPlanController subscriptionPlanController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllPlanByPlanIdList_ReturnsCorrectResponse() {
        // Given
        RequestDTO.SubscriptionRequestDTO requestBody = new RequestDTO.SubscriptionRequestDTO();
        requestBody.setPlanIdList(Arrays.asList("planId1", "planId2"));
        requestBody.setPlanType("type");

        ErrorResponseDTO.SubscriptionResponseDTO expectedResponse = new ErrorResponseDTO.SubscriptionResponseDTO();
        // Set your expected response here

        // Mocking the service method
        when(subscriptionFeignPlanService.getAllPlanByPlanIdList(requestBody.getPlanIdList(), requestBody.getPlanType()))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<ErrorResponseDTO.SubscriptionResponseDTO> responseEntity = subscriptionPlanController.getAllPlanByPlanIdList(requestBody);
        // Then
        assertEquals(expectedResponse, responseEntity.getBody());
    }
}