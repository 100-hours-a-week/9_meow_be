package meow_be.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequest {
    private Integer chatroomId;
    private Integer senderId;
    private String senderNickname;
    private String senderProfileImage;
    private String animalType;
    private String message;
}

