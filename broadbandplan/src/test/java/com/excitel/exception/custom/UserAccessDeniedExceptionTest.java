package com.excitel.exception.custom;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAccessDeniedExceptionTest {

    @Test
    void constructor_setsMessage() {
        // Arrange
        String errorMessage = "Access denied";

        // Act
        UserAccessDeniedException exception = new UserAccessDeniedException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
}
