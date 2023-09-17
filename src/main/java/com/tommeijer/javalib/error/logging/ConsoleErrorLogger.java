package com.tommeijer.javalib.error.logging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleErrorLogger implements ErrorLogger {

    @Override
    public void log(String message, Throwable t) {
        log.error(message, t);
    }
}
