package com.tommeijer.javalib.http;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.Map;

@Builder
@Getter
public class HttpRequest {
    @NonNull
    private final Method method;
    @NonNull
    private final String url;
    @Singular
    private final Map<String, Object> queryParams;
    @Singular
    private final Map<String, String> headers;
    private final Object body;

    public enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }
}
