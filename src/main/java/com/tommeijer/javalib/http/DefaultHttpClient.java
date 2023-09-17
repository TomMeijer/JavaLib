package com.tommeijer.javalib.http;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

public class DefaultHttpClient implements HttpClient {
    private final RestTemplate restTemplate;

    public DefaultHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T executeRequest(HttpRequest request, Class<T> responseType) {
        ResponseEntity<T> response = restTemplate.exchange(
                buildUrl(request),
                HttpMethod.valueOf(request.getMethod().name()),
                new HttpEntity<>(request.getBody(), buildHeaders(request)),
                responseType
        );
        return response.getBody();
    }

    private String buildUrl(HttpRequest request) {
        var uriBuilder = request.getUrl().startsWith("/")
                ? UriComponentsBuilder.fromPath(request.getUrl())
                : UriComponentsBuilder.fromHttpUrl(request.getUrl());
        if (request.getQueryParams() != null) {
            request.getQueryParams()
                    .entrySet()
                    .stream()
                    .filter(entry -> Objects.nonNull(entry.getValue()))
                    .forEach(entry -> uriBuilder.queryParam(entry.getKey(), entry.getValue()));
        }
        return uriBuilder.toUriString();
    }

    private HttpHeaders buildHeaders(HttpRequest request) {
        var headers = new HttpHeaders();
        if (request.getHeaders() != null) {
            request.getHeaders()
                    .entrySet()
                    .stream()
                    .filter(entry -> Objects.nonNull(entry.getValue()))
                    .forEach(entry -> headers.add(entry.getKey(), entry.getValue()));
        }
        return headers;
    }
}
