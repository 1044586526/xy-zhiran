package com.ruoyi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ResponseVO<T> success(T data) {
        return ResponseVO.<T>builder()
                .success(true)
                .message("操作成功")
                .data(data)
                .build();
    }

    public static <T> ResponseVO<T> success(String message, T data) {
        return ResponseVO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseVO<T> fail(String message) {
        return ResponseVO.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
