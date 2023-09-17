package com.tommeijer.javalib.http;

public interface HttpClient {

    <T> T executeRequest(HttpRequest request, Class<T> responseType);
}
