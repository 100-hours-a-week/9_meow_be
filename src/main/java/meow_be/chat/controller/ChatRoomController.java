package meow_be.chat.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.chat.dto.ChatRoomInfoDto;
import meow_be.login.security.TokenProvider;
import meow_be.common.exception.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final TokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<ChatRoomInfoDto> getSingleChatRoom(HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromHeader(request);

        if (token == null) {
            throw new UnauthorizedException("Authorization 헤더가 존재하지 않거나 Bearer 토큰이 아님");
        }

        Integer userId;
        try {
            userId = tokenProvider.getUserIdFromToken(token);
            if (!tokenProvider.validateToken(token, userId)) {
                throw new UnauthorizedException("유효하지 않은 토큰입니다.");
            }
        } catch (Exception e) {
            throw new UnauthorizedException("토큰 파싱 또는 검증 실패: " + e.getMessage());
        }

        ChatRoomInfoDto chatRoom = new ChatRoomInfoDto(1, "동물톡톡");
        return ResponseEntity.ok(chatRoom);
    }
}
