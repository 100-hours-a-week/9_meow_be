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
    private String animalType;
    private String message;

    public ChatMessageDto toDto() {
        return ChatMessageDto.builder()
                .chatroomId(this.chatroomId)
                .senderId(this.senderId)
                .animalType(this.animalType)
                .message(this.message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
