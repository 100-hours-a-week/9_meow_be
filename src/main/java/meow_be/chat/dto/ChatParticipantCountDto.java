package meow_be.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatParticipantCountDto {
    private Integer chatroomId;
    private Integer participantCount;
}
