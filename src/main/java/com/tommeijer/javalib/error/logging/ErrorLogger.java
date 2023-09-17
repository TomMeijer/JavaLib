package com.tommeijer.javalib.error.logging;

public interface ErrorLogger {

    void log(String message, Throwable t);
}
