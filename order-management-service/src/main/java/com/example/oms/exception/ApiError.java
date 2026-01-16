package com.example.oms.exception;

import java.time.Instant;
import java.util.List;

public record ApiError(
        String code,
        String message,
        Instant timestamp,
        String path,
        List<ValidationError> errors
) {
    public ApiError(String code, String message, Instant timestamp, String path) {
        this(code, message, timestamp, path, null);
    }
}
