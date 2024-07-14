package com.excitel.middleware;

import com.excitel.dto.ResponseDTO;
import com.excitel.external.TokenValidation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Objects;

import static com.excitel.constants.AppConstants.API_V1_PREFIX;

/**
 * Middleware filter for handling authorization and token validation.
 */
@Component
public class BroadbandMiddleware extends OncePerRequestFilter {

    @Autowired //NOSONAR
    private TokenValidation tokenValidationService;

    /**
     * Determines if the filter should be applied based on the request path and method.
     *
     * @param request The HTTP servlet request.
     * @return True if the filter should not be applied, false otherwise.
     * @throws ServletException If an error occurs during servlet processing.
     */

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return !path.startsWith(API_V1_PREFIX.getValue())
                || (path.equals("/api/v2/broadband") && request.getMethod().equals(HttpMethod.GET.name()))
                || path.equals("/api/v2/broadband/health")
                || path.equals("/api/v2/broadband/subscription-plan-detail");
    }

    /**
     * Performs the filtering logic for incoming requests.
     *
     * @param request     The HTTP servlet request.
     * @param response    The HTTP servlet response.
     * @param filterChain The filter chain for request processing.
     * @throws ServletException If an error occurs during servlet processing.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authToken = request.getHeader("Authorization");
        if (authToken == null || authToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization header missing");
            return;
        }
        //method called for validating token received
        ResponseEntity<ResponseDTO> responseEntity = tokenValidationService.isValid(authToken);
        // NULL pointer exception is handled in the if-else part thats why NOSONAR is used.
        if (Objects.nonNull(responseEntity) && Objects.nonNull( responseEntity.getBody()) && responseEntity.getBody().getStatus() == HttpStatus.OK) {//NOSONAR
            request.setAttribute("email", responseEntity.getBody().getEmail());//NOSONAR
            request.setAttribute("mobileNumber", responseEntity.getBody().getMobileNumber());//NOSONAR
            request.setAttribute("role", responseEntity.getBody().getRole());//NOSONAR
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Invalid authorization token");
        }
    }
}