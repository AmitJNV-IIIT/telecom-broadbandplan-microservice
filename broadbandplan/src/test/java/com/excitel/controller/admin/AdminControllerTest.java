package com.excitel.controller.admin;

import com.excitel.dto.BroadBandResponseDTO;
import com.excitel.dto.BroadbandPlanDTO;
import com.excitel.exception.custom.DatabaseConnectionException;
import com.excitel.exception.custom.NoPlanFoundException;
import com.excitel.exception.custom.UserAccessDeniedException;
import com.excitel.model.BroadbandPlan;
import com.excitel.service.admin.BroadbandService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private BroadbandService broadbandService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AdminController adminController;

   @Test
    void addBroadbandPlan_AdminRole_Success_test() {
        // Given
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        when(httpServletRequest.getAttribute("role")).thenReturn("ADMIN");
        when(broadbandService.addBroadbandPlan(broadbandPlan)).thenReturn(broadbandPlan);

        // When
        ResponseEntity<BroadbandPlanDTO> responseEntity = adminController.addBroadbandPlan(broadbandPlan, httpServletRequest);

        // Then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(broadbandPlan, responseEntity.getBody().getData());
    }

    @Test
    void addBroadbandPlan_NonAdminRole_ThrowsException_test() {
        // Given
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        when(httpServletRequest.getAttribute("role")).thenReturn("USER");

        // When, Then
        UserAccessDeniedException exception = assertThrows(UserAccessDeniedException.class, () ->
                adminController.addBroadbandPlan(broadbandPlan, httpServletRequest));

        assertEquals("Reserved route for Admin", exception.getMessage());
    }

    @Test
    void addBroadbandPlan_NullRole_ThrowsException_test() {
        // Given
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        when(httpServletRequest.getAttribute("role")).thenReturn(null);

        // When, Then
        UserAccessDeniedException exception = assertThrows(UserAccessDeniedException.class, () ->
                adminController.addBroadbandPlan(broadbandPlan, httpServletRequest));
        assertEquals("Reserved route for Admin", exception.getMessage());
    }

    @Test
    void addBroadbandPlan_AdminRole_ExceptionInService_ThrowsException_test() {
        // Given
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        when(httpServletRequest.getAttribute("role")).thenReturn("ADMIN");
        when(broadbandService.addBroadbandPlan(broadbandPlan)).thenThrow(new DatabaseConnectionException("Error Connecting to Database"));

        // When, Then
        assertThrows(DatabaseConnectionException.class, () ->
                adminController.addBroadbandPlan(broadbandPlan, httpServletRequest));
    }



    @Test
    void updateBroadbandPlan_AdminRole_Success_test() {
        // Given
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        String planId = "123";
        when(httpServletRequest.getAttribute("role")).thenReturn("ADMIN");
        when(broadbandService.updateBroadbandPlan(broadbandPlan, planId)).thenReturn(broadbandPlan);

        // When
        ResponseEntity<BroadbandPlanDTO> responseEntity = adminController.updateBroadbandPlan(broadbandPlan, planId, httpServletRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(broadbandPlan, responseEntity.getBody().getData());
    }

    @Test
    void updateBroadbandPlan_NonAdminRole_ThrowsException_test() {
        // Given
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        String planId = "123";
        when(httpServletRequest.getAttribute("role")).thenReturn("USER");

        // When, Then
        UserAccessDeniedException exception = assertThrows(UserAccessDeniedException.class, () ->
                adminController.updateBroadbandPlan(broadbandPlan, planId, httpServletRequest));
        assertEquals("Reserved route for Admin", exception.getMessage());
    }

    @Test
    void updateBroadbandPlan_InvalidPlanId_ThrowsException_test() {
        // Given
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        String planId = "invalidId";
        when(httpServletRequest.getAttribute("role")).thenReturn("ADMIN");
        when(broadbandService.updateBroadbandPlan(broadbandPlan, planId)).thenThrow(new NoPlanFoundException("Plan not found"));

        // When, Then
        NoPlanFoundException exception = assertThrows(NoPlanFoundException.class, () ->
                adminController.updateBroadbandPlan(broadbandPlan, planId, httpServletRequest));
        assertEquals("Plan not found", exception.getMessage());
    }

    @Test
    void deleteBroadbandPlan_AdminRole_SuccessfullyDeleted_test() {
        // Given
        String planId = "123";
        when(httpServletRequest.getAttribute("role")).thenReturn("ADMIN");
        when(broadbandService.deleteBroadbandPlan(planId)).thenReturn(true);

        // When
        ResponseEntity<BroadBandResponseDTO> responseEntity = adminController.deleteBroadbandPlan(planId, httpServletRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(planId + " Successfully deleted", responseEntity.getBody().getMessage());
    }

    @Test
    void deleteBroadbandPlan_NonAdminRole_ThrowsException_test() {
        // Given
        String planId = "123";
        when(httpServletRequest.getAttribute("role")).thenReturn("USER");

        // When, Then
        UserAccessDeniedException exception = assertThrows(UserAccessDeniedException.class, () ->
                adminController.deleteBroadbandPlan(planId, httpServletRequest));
        assertEquals("Reserved route for Admin", exception.getMessage());
    }

    @Test
    void deleteBroadbandPlan_ExceptionInService_ThrowsException_test() {
        // Given
        String planId = "validId";
        when(httpServletRequest.getAttribute("role")).thenReturn("ADMIN");
        when(broadbandService.deleteBroadbandPlan(planId)).thenThrow(new DatabaseConnectionException("Unexpected error occurred"));

        // When, Then
        assertThrows(DatabaseConnectionException.class, () ->
                adminController.deleteBroadbandPlan(planId, httpServletRequest));
    }
    @Test
    void deleteBroadbandPlan_AdminRole_AlreadyDeleted_test() {
        // Given
        String planId = "123";
        when(httpServletRequest.getAttribute("role")).thenReturn("ADMIN");
        when(broadbandService.deleteBroadbandPlan(planId)).thenReturn(false);

        // When
        ResponseEntity<BroadBandResponseDTO> responseEntity = adminController.deleteBroadbandPlan(planId, httpServletRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(planId + " Already deleted", responseEntity.getBody().getMessage());
    }
}
