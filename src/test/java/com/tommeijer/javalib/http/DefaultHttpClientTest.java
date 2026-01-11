package com.tommeijer.javalib.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void executeRequest_GetWithQueryParamsAndHeaders_ShouldCreateCorrectRequest() {
        var request = HttpRequest.builder()
                .method(HttpRequest.Method.GET)
                .url("https://myapi.com")
                .queryParam("param1", "value1")
                .queryParam("param2", "value2")
                .header("header1", "headerValue1")
                .header("header2", "headerValue2")
                .build();
        var responseEntity = new ResponseEntity<>("body", HttpStatus.OK);
        
        var expectedUrl = request.getUrl() + "?param1=value1&param2=value2";
        var expectedMethod = HttpMethod.valueOf(request.getMethod().name());
        
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(expectedMethod),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);
        
        var result = httpClient.executeRequest(request, String.class);
        
        assertThat(result, is(responseEntity.getBody()));
        
        var entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(expectedUrl), eq(expectedMethod), entityCaptor.capture(), eq(String.class));
        
        HttpEntity<?> capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getBody(), is(nullValue()));
        assertThat(capturedEntity.getHeaders().getFirst("header1"), is("headerValue1"));
        assertThat(capturedEntity.getHeaders().getFirst("header2"), is("headerValue2"));
    }

    @Test
    void executeRequest_PostWithBodyAndHeaders_ShouldCreateCorrectRequest() {
        var request = HttpRequest.builder()
                .method(HttpRequest.Method.POST)
                .url("https://myapi.com")
                .body("body")
                .header("header1", "headerValue1")
                .header("header2", "headerValue2")
                .build();
        var responseEntity = new ResponseEntity<>("body", HttpStatus.OK);
        
        var expectedUrl = request.getUrl();
        var expectedMethod = HttpMethod.valueOf(request.getMethod().name());
        
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(expectedMethod),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);
        
        var result = httpClient.executeRequest(request, String.class);
        
        assertThat(result, is(responseEntity.getBody()));
        
        var entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(expectedUrl), eq(expectedMethod), entityCaptor.capture(), eq(String.class));
        
        HttpEntity<?> capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getBody(), is(request.getBody()));
        assertThat(capturedEntity.getHeaders().getFirst("header1"), is("headerValue1"));
        assertThat(capturedEntity.getHeaders().getFirst("header2"), is("headerValue2"));
    }

    @Test
    void executeRequest_UrlAsPath_ShouldCreateCorrectRequest() {
        var request = HttpRequest.builder()
                .method(HttpRequest.Method.GET)
                .url("/path")
                .queryParam("param1", "value1")
                .queryParam("param2", "value2")
                .build();
        var responseEntity = new ResponseEntity<>("body", HttpStatus.OK);
        
        var expectedUrl = request.getUrl() + "?param1=value1&param2=value2";
        var expectedMethod = HttpMethod.valueOf(request.getMethod().name());
        
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(expectedMethod),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);
        
        var result = httpClient.executeRequest(request, String.class);
        
        assertThat(result, is(responseEntity.getBody()));
        
        var entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(expectedUrl), eq(expectedMethod), entityCaptor.capture(), eq(String.class));
        
        HttpEntity<?> capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getBody(), is(nullValue()));
    }
}
