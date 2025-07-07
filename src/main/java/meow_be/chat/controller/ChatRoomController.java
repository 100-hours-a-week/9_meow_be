package meow_be.chat.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.chat.dto.ChatRoomInfoDto;
import meow_be.login.security.TokenProvider;
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

        if (token == null || !tokenProvider.validateToken(token, tokenProvider.getUserIdFromToken(token))) {
            return ResponseEntity.status(401).build();
        }
        Integer userId = tokenProvider.getUserIdFromToken(token);

        ChatRoomInfoDto chatRoom = new ChatRoomInfoDto(1, "동물톡톡");
        return ResponseEntity.ok(chatRoom);
    }
}
