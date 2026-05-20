package com.hemant.instagram.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;
    private T data;
    private ApiError error;

    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    public ApiResponse(ApiError error) {
        this();
        this.error = error;
    }
}
