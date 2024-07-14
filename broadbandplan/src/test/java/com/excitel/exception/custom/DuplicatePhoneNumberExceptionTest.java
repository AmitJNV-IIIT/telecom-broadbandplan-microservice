package com.excitel.exception.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DuplicatePhoneNumberExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        // Test data
        String message = "Test Message";

        // Create exception
        DuplicatePhoneNumberException exception = new DuplicatePhoneNumberException(message);

        // Assertions
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
}
