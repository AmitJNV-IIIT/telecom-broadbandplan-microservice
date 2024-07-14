package com.excitel.external;

import com.excitel.dto.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for token validation.
 */

//@FeignClient(name = "AUTH-SERVICE",url = "${base_url_localhost}")
@FeignClient(name = "AUTH-SERVICE", url = "${base-url-stage}")
public interface TokenValidation {
    /**
     * Validates the provided token with the authentication service.
     *
     * @param token The authorization token
     * @return ResponseEntity containing the validation response
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @GetMapping("/auth/check-token")
    ResponseEntity<ResponseDTO> isValid(@RequestHeader("Authorization") String token);
}