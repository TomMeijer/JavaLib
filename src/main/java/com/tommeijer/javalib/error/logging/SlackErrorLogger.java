package com.tommeijer.javalib.error.logging;

import com.tommeijer.javalib.client.slack.SlackApiException;
import com.tommeijer.javalib.client.slack.SlackResponse;
import com.tommeijer.javalib.http.HttpClient;
import com.tommeijer.javalib.http.HttpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

import java.io.PrintWriter;
import java.io.StringWriter;

@RequiredArgsConstructor
public class SlackErrorLogger implements ErrorLogger {
    private final String channel;
    private final HttpClient httpClient;

    @Override
    public void log(String message, Throwable t) {
        var params = new LinkedMultiValueMap<String, Object>();
        params.add("channels", channel);
        params.add("content", getStackTrace(t));
        params.add("title", "error_" + System.currentTimeMillis());

        var request = HttpRequest.builder()
                .method(HttpRequest.Method.POST)
                .url("/files.upload")
                .body(params)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        var response = httpClient.executeRequest(request, SlackResponse.class);
        if (!response.isOk()) {
            throw new SlackApiException(response.getError());
        }
    }

    private String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}
