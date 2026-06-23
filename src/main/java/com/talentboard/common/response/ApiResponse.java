package com.talentboard.common.response;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {
    //se habilida el uso de Genericos con la primera <T> 
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
