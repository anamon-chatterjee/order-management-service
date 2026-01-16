package com.example.oms.exception;

public record ValidationError(
        String field,
        String message
) {}
