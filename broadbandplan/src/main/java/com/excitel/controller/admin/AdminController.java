package com.excitel.controller.admin;

import com.excitel.dto.BroadBandResponseDTO;
import com.excitel.dto.BroadbandPlanDTO;
import com.excitel.exception.custom.UserAccessDeniedException;
import com.excitel.model.BroadbandPlan;
import com.excitel.service.admin.BroadbandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.excitel.constants.AppConstants.*;

/**
 * Controller class for handling admin-related broadband plan operations.
 */
@RestController
@Tag(name = "Broadband Admin Controller", description = "broadband Admin Controller APIs")
@RequestMapping("/api/v2/broadband")
@CrossOrigin
public class AdminController {

    @Autowired //NOSONAR
    private BroadbandService broadbandService;

    /**
     * Adds a new broadband plan.
     *
     * @param broadbandPlan       The broadband plan to be added
     * @param httpServletRequest The HTTP servlet request
     * @return ResponseEntity containing either a success or error response
     */
    @PostMapping
    public ResponseEntity<BroadbandPlanDTO> addBroadbandPlan(@Valid @RequestBody BroadbandPlan broadbandPlan, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute(REQUEST_ATTRIBUTE.getValue());
        // check if the role is admin
        if (Objects.equals(role, ADMIN.getValue())) {
            BroadbandPlan addedBroadbandPlan = broadbandService.addBroadbandPlan(broadbandPlan);
            // Build the success response
            BroadbandPlanDTO response = BroadbandPlanDTO.builder().status(HttpStatus.CREATED).data(addedBroadbandPlan).build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else{
            // throw unauthorized error response for users
            throw new UserAccessDeniedException(USER_DENIED_MESSAGE.getValue());
        }
    }
    /**
     * Updates an existing broadband plan.
     *
     * @param broadbandPlan       The updated broadband plan
     * @param planId              The ID of the plan to be updated
     * @param httpServletRequest The HTTP servlet request
     * @return ResponseEntity containing either a success or error response
     */
    @PutMapping("/{planId}")
    public ResponseEntity<BroadbandPlanDTO> updateBroadbandPlan(@RequestBody BroadbandPlan broadbandPlan, @PathVariable String planId, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute(REQUEST_ATTRIBUTE.getValue());
        // check if the role is admin
        if (Objects.equals(role, ADMIN.getValue())) {
            BroadbandPlan updatedBroadbandPlan = broadbandService.updateBroadbandPlan(broadbandPlan, planId);
            // Build the success response
            BroadbandPlanDTO response = BroadbandPlanDTO.builder().status(HttpStatus.OK).data(updatedBroadbandPlan).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            // throw unauthorized error response for users
            throw new UserAccessDeniedException(USER_DENIED_MESSAGE.getValue());
        }
    }

    /**
     * Deletes a broadband plan by ID.
     *
     * @param planId              The ID of the plan to be deleted
     * @param httpServletRequest The HTTP servlet request
     * @return ResponseEntity containing either a success or error response
     */

    @DeleteMapping("/{planId}")
    public ResponseEntity<BroadBandResponseDTO> deleteBroadbandPlan(@PathVariable String planId, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute(REQUEST_ATTRIBUTE.getValue());
        // check if the role is admin
        if (Objects.equals(role, ADMIN.getValue())) {
            boolean verifyDelete = broadbandService.deleteBroadbandPlan(planId);
            String message;
            if (verifyDelete) message = planId + " Successfully deleted";
            else message = planId + " Already deleted";
            // Build the success response
            BroadBandResponseDTO response = BroadBandResponseDTO.builder().status(HttpStatus.OK).message(message).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            // throw unauthorized error response for users
            throw new UserAccessDeniedException(USER_DENIED_MESSAGE.getValue());
        }
    }
}