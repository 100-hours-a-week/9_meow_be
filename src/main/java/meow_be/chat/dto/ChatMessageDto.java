package meow_be.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String chatRoomId;
    private String senderId;
    private String message;
    private LocalDateTime timestamp;
}

