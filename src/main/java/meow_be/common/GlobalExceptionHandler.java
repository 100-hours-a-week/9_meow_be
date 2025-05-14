package meow_be.common;

import meow_be.posts.controller.PostController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PostController.UnauthorizedException.class)
    public ResponseEntity<ApiResponse<String>> handleUnauthorizedException(PostController.UnauthorizedException ex) {
        ApiResponse<String> response = ApiResponse.error(HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

}
