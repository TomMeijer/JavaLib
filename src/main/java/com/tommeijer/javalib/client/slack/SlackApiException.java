package com.tommeijer.javalib.client.slack;

public class SlackApiException extends RuntimeException {
    public SlackApiException(String message) {
        super(message);
    }
}
