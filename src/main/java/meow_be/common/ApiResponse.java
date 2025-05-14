package meow_be.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int statusCode;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, data);
    }

    public static <T> ApiResponse<T> error(int statusCode) {
        return new ApiResponse<>(statusCode, null);
    }
    public static <T> ApiResponse<T> error(int statusCode, T message) {
        return new ApiResponse<>(statusCode, message);
    }

}
