package meow_be.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatParticipantEventDto {
    private Integer chatroomId;
    private Integer participantCount;
    private String message;
}
