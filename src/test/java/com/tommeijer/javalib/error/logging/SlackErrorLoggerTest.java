package com.tommeijer.javalib.error.logging;

import com.tommeijer.javalib.client.slack.SlackApiException;
import com.tommeijer.javalib.client.slack.SlackResponse;
import com.tommeijer.javalib.http.HttpClient;
import com.tommeijer.javalib.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlackErrorLoggerTest {
    private static final String CHANNEL = "test-channel";

    @Mock
    private HttpClient httpClient;

    private SlackErrorLogger slackErrorLogger;

    @BeforeEach
    void setUp() {
        slackErrorLogger = new SlackErrorLogger(CHANNEL, httpClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    void log_SuccessfulRequest_ShouldExecuteProperly() {
        // Arrange
        String message = "Test message";
        Throwable throwable = new RuntimeException("Test exception");
        SlackResponse response = new SlackResponse();
        response.setOk(true);

        when(httpClient.executeRequest(any(HttpRequest.class), eq(SlackResponse.class))).thenReturn(response);

        // Act
        slackErrorLogger.log(message, throwable);

        // Assert
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).executeRequest(requestCaptor.capture(), eq(SlackResponse.class));

        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals(HttpRequest.Method.POST, capturedRequest.getMethod());
        assertEquals("/files.upload", capturedRequest.getUrl());
        assertEquals(MediaType.APPLICATION_FORM_URLENCODED_VALUE, capturedRequest.getHeaders().get(HttpHeaders.CONTENT_TYPE));

        assertTrue(capturedRequest.getBody() instanceof MultiValueMap);
        MultiValueMap<String, Object> body = (MultiValueMap<String, Object>) capturedRequest.getBody();
        assertEquals(CHANNEL, body.getFirst("channels"));
        assertNotNull(body.getFirst("content"));
        assertTrue(body.getFirst("content").toString().contains("java.lang.RuntimeException: Test exception"));
        assertNotNull(body.getFirst("title"));
        assertTrue(body.getFirst("title").toString().startsWith("error_"));
    }

    @Test
    void log_SlackResponseNotOk_ShouldThrowSlackApiException() {
        // Arrange
        String message = "Test message";
        Throwable throwable = new RuntimeException("Test exception");
        SlackResponse response = new SlackResponse();
        response.setOk(false);
        response.setError("invalid_auth");

        when(httpClient.executeRequest(any(HttpRequest.class), eq(SlackResponse.class))).thenReturn(response);

        // Act & Assert
        SlackApiException exception = assertThrows(SlackApiException.class, () -> slackErrorLogger.log(message, throwable));
        assertEquals("invalid_auth", exception.getMessage());
    }
}
