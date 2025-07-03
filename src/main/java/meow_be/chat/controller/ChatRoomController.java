package meow_be.chat.controller;

import lombok.RequiredArgsConstructor;
import meow_be.chat.dto.ChatRoomInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    @GetMapping
    public ResponseEntity<ChatRoomInfoDto> getSingleChatRoom() {
        ChatRoomInfoDto chatRoom = new ChatRoomInfoDto(1, "동물톡톡");
        return ResponseEntity.ok(chatRoom);
    }
}
