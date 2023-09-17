package com.tommeijer.javalib.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

@ExtendWith(MockitoExtension.class)
class DefaultHttpClientTest {
    @Mock
    private RestTemplate restTemplate;

    private DefaultHttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = new DefaultHttpClient(restTemplate);
    }

    @Test
    void executeRequest_getWithQueryParamsAndHeaders_correctRequest() {
        var request = HttpRequest.builder()
                .method(HttpRequest.Method.GET)
                .url("https://myapi.com")
                .queryParam("param1", "value1")
                .queryParam("param2", "value2")
                .header("header1", "headerValue1")
                .header("header2", "headerValue2")
                .build();
        var responseEntity = new ResponseEntity<>("body", HttpStatus.OK);
        when(restTemplate.exchange(
                eq(request.getUrl() + "?param1=value1&param2=value2"),
                eq(HttpMethod.valueOf(request.getMethod().name())),
                argThat(allOf(
                        hasProperty("body", is(nullValue())),
                        hasProperty("headers", allOf(
                                hasEntry("header1", List.of("headerValue1")),
                                hasEntry("header2", List.of("headerValue2"))
                        ))
                )),
                eq(String.class)
        )).thenReturn(responseEntity);
        var result = httpClient.executeRequest(request, String.class);
        assertThat(result, is(responseEntity.getBody()));
    }

    @Test
    void executeRequest_postWithBodyAndHeaders_correctRequest() {
        var request = HttpRequest.builder()
                .method(HttpRequest.Method.POST)
                .url("https://myapi.com")
                .body("body")
                .header("header1", "headerValue1")
                .header("header2", "headerValue2")
                .build();
        var responseEntity = new ResponseEntity<>("body", HttpStatus.OK);
        when(restTemplate.exchange(
                eq(request.getUrl()),
                eq(HttpMethod.valueOf(request.getMethod().name())),
                argThat(allOf(
                        hasProperty("body", is(request.getBody())),
                        hasProperty("headers", allOf(
                                hasEntry("header1", List.of("headerValue1")),
                                hasEntry("header2", List.of("headerValue2"))
                        ))
                )),
                eq(String.class)
        )).thenReturn(responseEntity);
        var result = httpClient.executeRequest(request, String.class);
        assertThat(result, is(responseEntity.getBody()));
    }

    @Test
    void executeRequest_urlAsPath_correctRequest() {
        var request = HttpRequest.builder()
                .method(HttpRequest.Method.GET)
                .url("/path")
                .queryParam("param1", "value1")
                .queryParam("param2", "value2")
                .build();
        var responseEntity = new ResponseEntity<>("body", HttpStatus.OK);
        when(restTemplate.exchange(
                eq(request.getUrl() + "?param1=value1&param2=value2"),
                eq(HttpMethod.valueOf(request.getMethod().name())),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);
        var result = httpClient.executeRequest(request, String.class);
        assertThat(result, is(responseEntity.getBody()));
    }
}
