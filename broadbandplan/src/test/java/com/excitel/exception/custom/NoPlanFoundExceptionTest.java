package com.excitel.exception.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NoPlanFoundExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        // Test data
        String message = "Test Message";

        // Create exception
        NoPlanFoundException exception = new NoPlanFoundException(message);

        // Assertions
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
}
