package meow_be.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiContentResponse {

    @JsonProperty("status_code")
    private int statusCode;

    private String message;

    private String data;

    public static AiContentResponse success(String data) {
        return new AiContentResponse(200, "Success", data);
    }

    public static AiContentResponse success(String data, String message) {
        return new AiContentResponse(200, message, data);
    }

    public static AiContentResponse error(int statusCode) {
        return new AiContentResponse(statusCode, "Error", null);
    }

    public static AiContentResponse error(int statusCode, String message) {
        return new AiContentResponse(statusCode, message, null);
    }

    public static AiContentResponse error(int statusCode, String message, String data) {
        return new AiContentResponse(statusCode, message, data);
    }
}
