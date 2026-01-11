package com.tommeijer.javalib.error.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DelegatingErrorLoggerTest {

    @Mock
    private ErrorLogger logger1;

    @Mock
    private ErrorLogger logger2;

    private DelegatingErrorLogger delegatingErrorLogger;

    @BeforeEach
    void setUp() {
        delegatingErrorLogger = new DelegatingErrorLogger(List.of(logger1, logger2));
    }

    @Test
    void log_ShouldDelegateToAllLoggers() {
        // Arrange
        String message = "test message";
        Throwable t = new RuntimeException("test exception");

        // Act
        delegatingErrorLogger.log(message, t);

        // Assert
        verify(logger1).log(message, t);
        verify(logger2).log(message, t);
    }

    @Test
    void log_WhenDelegateThrowsException_ShouldContinueToNextDelegate() {
        // Arrange
        String message = "test message";
        Throwable t = new RuntimeException("test exception");

        doThrow(new RuntimeException("failed")).when(logger1).log(anyString(), any(Throwable.class));

        // Act
        delegatingErrorLogger.log(message, t);

        // Assert
        verify(logger1).log(message, t);
        verify(logger2).log(message, t);
    }

    @Test
    void log_WithEmptyDelegates_ShouldNotThrowException() {
        // Arrange
        delegatingErrorLogger = new DelegatingErrorLogger(List.of());

        // Act & Assert (no exception expected)
        delegatingErrorLogger.log("message", new RuntimeException());
    }
}
