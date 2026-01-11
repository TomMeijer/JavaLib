package com.tommeijer.javalib.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BearerAuthInterceptorTest {

    @Mock
    private TokenProvider<String> tokenProvider;

    @Mock
    private HttpRequest request;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private ClientHttpResponse response;

    private BearerAuthInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new BearerAuthInterceptor(tokenProvider);
    }

    @Test
    void intercept_ShouldAddBearerAuthHeaderAndExecute() throws IOException {
        String token = "test-token";
        byte[] body = "body".getBytes();
        HttpHeaders headers = new HttpHeaders();

        when(tokenProvider.getToken()).thenReturn(token);
        when(request.getHeaders()).thenReturn(headers);
        when(execution.execute(request, body)).thenReturn(response);

        ClientHttpResponse result = interceptor.intercept(request, body, execution);

        assertEquals(response, result);
        assertEquals("Bearer " + token, headers.getFirst(HttpHeaders.AUTHORIZATION));
    }
}
