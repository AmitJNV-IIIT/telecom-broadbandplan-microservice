package com.excitel.middleware;

import com.excitel.dto.ResponseDTO;
import com.excitel.external.TokenValidation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BroadbandMiddlewareTest {

    @InjectMocks
    private BroadbandMiddleware broadbandMiddleware;

    @Mock
    private TokenValidation tokenValidationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShouldNotFilter() throws Exception {
        when(request.getServletPath()).thenReturn("/api/v2/broadband/health");
        boolean result = broadbandMiddleware.shouldNotFilter(request);
        assertEquals(true, result);
    }

    @Test
    public void testShouldNotFilter_WithInvalidServletPath() throws Exception {
        when(request.getServletPath()).thenReturn("/api/v1/broadband/health");
        boolean result = broadbandMiddleware.shouldNotFilter(request);
        assertEquals(true, result);
    }
    @Test
    public void testShouldNotFilter_WithSubscriptionPlanDetailPath() throws Exception {
        when(request.getServletPath()).thenReturn("/api/v2/broadband/subscription-plan-detail");
        boolean result = broadbandMiddleware.shouldNotFilter(request);
        assertEquals(true, result);

    }

    @Test
    public void testDoFilterInternal_ValidAuthorizationToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("validToken");
        when(tokenValidationService.isValid("validToken")).thenReturn(ResponseEntity.ok(new ResponseDTO(HttpStatus.OK, "message", "email", "mobileNumber", "role")));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        broadbandMiddleware.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_InvalidAuthorizationToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("invalidToken");
        when(tokenValidationService.isValid("invalidToken")).thenReturn(ResponseEntity.ok(new ResponseDTO(HttpStatus.FORBIDDEN, "message", null, null, null)));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        broadbandMiddleware.doFilterInternal(request, response, filterChain);

        assertEquals("Invalid authorization token", stringWriter.toString());
        verify(filterChain, times(0)).doFilter(any(), any());
    }

    @Test
    public void testDoFilterInternal_MissingAuthorizationToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        broadbandMiddleware.doFilterInternal(request, response, filterChain);

        assertEquals("Authorization header missing", stringWriter.toString());
        verify(filterChain, times(0)).doFilter(any(), any());
    }

    @Test
    public void testShouldNotFilter_WithApiV2BroadbandGET() throws Exception {
        when(request.getServletPath()).thenReturn("/api/v2/broadband");
        when(request.getMethod()).thenReturn(HttpMethod.GET.name());
        boolean result = broadbandMiddleware.shouldNotFilter(request);
        assertEquals(true, result);
    }

    @Test
    public void testDoFilterInternal_NonNullResponseEntityAndBody_OKStatus() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("validToken");
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "message", "email", "mobileNumber", "role");
        ResponseEntity<ResponseDTO> responseEntity = ResponseEntity.ok(responseDTO);
        when(tokenValidationService.isValid("validToken")).thenReturn(responseEntity);

        broadbandMiddleware.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_NullAuthorizationToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        broadbandMiddleware.doFilterInternal(request, response, filterChain);

        assertEquals("Authorization header missing", stringWriter.toString());
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_EmptyAuthorizationToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("");
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        broadbandMiddleware.doFilterInternal(request, response, filterChain);

        assertEquals("Authorization header missing", stringWriter.toString());
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    public void shouldNotFilterBroadbandPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v2/broadband");
        assertTrue(broadbandMiddleware.shouldNotFilter(request));
    }

    @Test
    public void shouldNotFilterSubscriptionPlanDetailPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v2/broadband/subscription-plan-detail");
        assertTrue(broadbandMiddleware.shouldNotFilter(request));
    }

    @Test
    public void doFilterInternalWithValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "valid_token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenValidationService.isValid(anyString())).thenReturn(ResponseEntity.ok(new ResponseDTO()));

        broadbandMiddleware.doFilterInternal(request, response, null);

//        assertEquals(200, response.getStatus());
    }

    @Test
    public void doFilterInternalWithInvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "invalid_token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenValidationService.isValid(anyString())).thenReturn(ResponseEntity.ok(null));

        broadbandMiddleware.doFilterInternal(request, response, null);

        assertEquals(403, response.getStatus());
    }


}
