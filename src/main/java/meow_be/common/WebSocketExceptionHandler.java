package meow_be.common;

import lombok.extern.slf4j.Slf4j;
import meow_be.common.exception.UnauthorizedException;
import meow_be.config.JwtHandshakeInterceptor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WebSocketExceptionHandler {

    @MessageExceptionHandler(UnauthorizedException.class)
    @SendToUser("/queue/errors")
    public ApiResponse<String> handleUnauthorized(UnauthorizedException ex) {
        return ApiResponse.error(401, ex.getMessage());
    }

    @MessageExceptionHandler(JwtHandshakeInterceptor.ChatRoomFullException.class)
    @SendToUser("/queue/errors")
    public ApiResponse<String> handleChatRoomFull(JwtHandshakeInterceptor.ChatRoomFullException ex) {
        return ApiResponse.error(429, ex.getMessage());
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser("/queue/errors")
    public ApiResponse<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ApiResponse.error(400, ex.getMessage());
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ApiResponse<String> handleUnknown(Exception ex) {
        log.error("[WebSocket 예외]", ex);
        return ApiResponse.error(500, "서버 오류: " + ex.getMessage());
    }
}
