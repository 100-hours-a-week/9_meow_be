package meow_be.notification.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.common.exception.UnauthorizedException;
import meow_be.login.security.TokenProvider;
import meow_be.notification.dto.NotificationResponseDto;
import meow_be.notification.service.NotificationService;
import meow_be.posts.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final TokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponseDto>> getMyNotifications(
            HttpServletRequest request,
            Pageable pageable
    ) {
        String token = tokenProvider.extractTokenFromHeader(request);
        if (token == null) {
            throw new UnauthorizedException("인증 토큰이 없습니다.");
        }

        Integer userId;
        try {
            userId = tokenProvider.getUserIdFromToken(token);
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }

        PageResponse<NotificationResponseDto> response =
                notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(response);
    }
}
