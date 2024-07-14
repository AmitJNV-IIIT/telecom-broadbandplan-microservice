package com.excitel.controller.user;

import com.excitel.dto.*;
import com.excitel.exception.custom.DuplicatePhoneNumberException;
import com.excitel.model.BroadbandConnection;
import com.excitel.service.user.BroadbandUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private BroadbandUserService broadbandUserService;

    @InjectMocks
    private UserController userController;

    @Test
    void testGetHealth() {
        assertEquals("live", userController.getHealth());
    }

    @Test
    void testGetBroadbandPlans() {
        // Given
        RequestDTO requestDTO = new RequestDTO();
        BroadbandPlanListDTO expectedDTO = new BroadbandPlanListDTO();
        ResponseEntity<BroadbandPlanListDTO> expectedResponse = ResponseEntity.ok().body(expectedDTO);

        // Mock the behavior of broadbandUserService to return expectedDTO
        when(broadbandUserService.getBroadbandPlanWithQuery(any(RequestDTO.class))).thenReturn(expectedDTO.getData());

        // When
        ResponseEntity<BroadbandPlanListDTO> responseEntity = userController.getBroadbandPlans(requestDTO);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    }


    @Test
    void testCreateBroadbandConnection() {
        // Given
        BroadbandConnection broadbandConnection = new BroadbandConnection();
        String phoneNumber = "1234567890";
        when(broadbandUserService.createBroadbandConnection(any(BroadbandConnection.class), eq(phoneNumber)))
                .thenReturn(new BroadbandConnection());

        // When
        ResponseEntity<BroadbandConnectionDTO> responseEntity = userController.createBroadbandConnection(broadbandConnection, phoneNumber);

        // Then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void testGetConnectionDetailsForUser() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        String mobileNumber = "1234567890";
        when(request.getAttribute("mobileNumber")).thenReturn(mobileNumber);

        BroadbandConnection broadbandConnection = new BroadbandConnection();
        broadbandConnection.setStatus("Active");
        when(broadbandUserService.getConnectionDetailsForUser(anyString(), anyString()))
                .thenReturn(broadbandConnection);

        // When
        ResponseEntity<ConnectionResponseDTO> responseEntity = userController.getConnectionDetailsForUser(request);
        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }
    @Test
    void testGetConnectionDetailsForUserWhenDuplicatePhoneNumberExceptionThrown() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        String mobileNumber = "1234567890";
        when(request.getAttribute("mobileNumber")).thenReturn(mobileNumber);

        when(broadbandUserService.getConnectionDetailsForUser(anyString(), anyString()))
                .thenThrow(new DuplicatePhoneNumberException("Duplicate phone number"));

        // When, Then
        assertThrows(DuplicatePhoneNumberException.class, () -> {
            userController.getConnectionDetailsForUser(request);
        });

    }

    @Test
    void shouldReturnNullWhenNoConnectionDetails() {
        // Arrange
        String mobileNumber = "1234567890";
        String status = "Active";

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("mobileNumber")).thenReturn(mobileNumber);

        // When broadbandConnection is null, Optional.ofNullable will create an Optional.empty()
        when(broadbandUserService.getConnectionDetailsForUser(anyString(), anyString())).thenReturn(null);

        // Act
        ResponseEntity<ConnectionResponseDTO> response = userController.getConnectionDetailsForUser(request);

        // Assert
        assertNull(response);
    }

}