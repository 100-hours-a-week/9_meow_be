package meow_be.chat.service;

import lombok.RequiredArgsConstructor;
import meow_be.chat.dto.ChatParticipantCountDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParticipantNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyCount(int chatroomId, int count) {
        ChatParticipantCountDto payload = new ChatParticipantCountDto(chatroomId, count);
        messagingTemplate.convertAndSend("/sub/chatroom." + chatroomId + ".participants", payload);
    }
}
