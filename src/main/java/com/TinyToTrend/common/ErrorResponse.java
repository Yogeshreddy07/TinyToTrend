package com.tinytotrend.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Standardized error response format for API errors.
 */
public class ErrorResponse {

    private String error;
    private String timestamp;
    private String path;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ErrorResponse(String error, String path) {
        this();
        this.error = error;
        this.path = path;
    }

    // Getters and Setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
