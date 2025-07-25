package com.lopez.userhandler.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ApiResponse<T> {
    private int status;
    private T body;
    private String message;

    public ApiResponse(int status, T body) {
        this.status = status;
        this.body = body;
    }

    public ApiResponse(int status, T body, String message) {
        this.status = status;
        this.body = body;
        this.message = message;
    }

    public static <T> ApiResponse<T> ok(T body) {
        return new ApiResponse<>(200, body);
    }

    public static <T> ApiResponse<T> ok(T body, String message) {
        return new ApiResponse<>(200, body, message);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, null, message);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, null, message);
    }

    public static <T> ApiResponse<T> conflict(String message) {
        return new ApiResponse<>(409, null, message);
    }

    public static <T> ApiResponse<T> internalServerError(String message) {
        return new ApiResponse<>(500, null, message);
    }

    public int getStatus() {
        return status;
    }

    public T getBody() {
        return body;
    }

    public String getMessage() {
        return message;
    }
}