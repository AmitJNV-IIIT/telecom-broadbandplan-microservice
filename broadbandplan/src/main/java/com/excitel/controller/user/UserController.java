package com.excitel.controller.user;

import com.excitel.dto.BroadbandConnectionDTO;
import com.excitel.dto.BroadbandPlanListDTO;
import com.excitel.dto.ConnectionResponseDTO;
import com.excitel.dto.RequestDTO;
import com.excitel.exception.custom.DuplicatePhoneNumberException;
import com.excitel.model.BroadbandConnection;
import com.excitel.service.user.BroadbandUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller class for handling user-related broadband operations.
 */
@RestController
@Tag(name = "Broadband User Controller", description = "broadband User Controller APIs")
@RequestMapping("/api/v2/broadband")
@CrossOrigin
public class UserController {

    @Autowired //NOSONAR
    private BroadbandUserService broadbandUserService;

    /**
     * Endpoint to check the health of the application.
     *
     * @return A string indicating the health status ("live").
     */

    @GetMapping("/health")
    public String getHealth() {
        return "live";
    }
    /**
     * Retrieves broadband plans based on the provided parameters.
     *
     * @param params The request parameters for filtering broadband plans
     * @return ResponseEntity containing a list of broadband plans or an error response
     */

    @GetMapping
    public ResponseEntity<BroadbandPlanListDTO> getBroadbandPlans(@ModelAttribute RequestDTO params) {
        // Call the service to get broadband plans based on the provided parameters
        BroadbandPlanListDTO response = BroadbandPlanListDTO.builder()
                .status(HttpStatus.OK)
                .data(broadbandUserService.getBroadbandPlanWithQuery(params))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    /**
     * Creates a new broadband connection for a user.
     *
     * @param broadbandConnection The broadband connection details
     * @param phoneNumber         The mobile number of the user (from request attribute)
     * @return ResponseEntity containing either a success or error response
     */

    @PostMapping("/connection/new")
    public ResponseEntity<BroadbandConnectionDTO> createBroadbandConnection(@RequestBody BroadbandConnection broadbandConnection,
                                                       @RequestAttribute(value = "mobileNumber") String phoneNumber) {
        // Call the service to create a new broadband connection for the user
            BroadbandConnection createdBroadbandConnection = broadbandUserService.createBroadbandConnection(broadbandConnection,phoneNumber);
            BroadbandConnectionDTO response = BroadbandConnectionDTO.builder().status(HttpStatus.CREATED).message("Created new broadband connection successfully").data(createdBroadbandConnection).build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
    /**
     * Retrieves connection details for a user.
     *
     * @param request The HTTP servlet request
     * @return ResponseEntity containing either connection details or an error response
     */
    @GetMapping("/connection/me")
    public ResponseEntity<ConnectionResponseDTO> getConnectionDetailsForUser(HttpServletRequest request) {
        String status = "Active";
        String mobileNumber = (String) request.getAttribute("mobileNumber");
        try{
            // Call the service to get connection details for the user

            Optional<BroadbandConnection> broadbandConnection = Optional.ofNullable(broadbandUserService.getConnectionDetailsForUser(mobileNumber,status));
            if (broadbandConnection.isPresent()) {
                ConnectionResponseDTO response = ConnectionResponseDTO.builder()
                        .status(HttpStatus.OK)
                        .message("Connection fetched successfully")
                        .broadbandConnection(broadbandConnection.get())
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }catch (DuplicatePhoneNumberException e){
            // Handle duplicate phone number exception

            throw new DuplicatePhoneNumberException(e.getMessage());
        }
        return null;
    }
}
