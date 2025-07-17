package meow_be.notification.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.login.security.TokenProvider;
import meow_be.notification.sse.SseEmitterManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationSseController {

    private final SseEmitterManager sseEmitterManager;
    private final TokenProvider tokenProvider;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromHeader(request);
        Integer userId = tokenProvider.getUserIdFromToken(token);
        return sseEmitterManager.connect(userId);
    }
}