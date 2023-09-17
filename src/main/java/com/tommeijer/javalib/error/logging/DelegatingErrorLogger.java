package com.tommeijer.javalib.error.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DelegatingErrorLogger implements ErrorLogger {
    private final List<ErrorLogger> delegates;

    @Override
    public void log(String message, Throwable t) {
        for (var delegate : delegates) {
            try {
                delegate.log(message, t);
            } catch (Exception e) {
                log.error("Failed to log error", e);
            }
        }
    }
}
