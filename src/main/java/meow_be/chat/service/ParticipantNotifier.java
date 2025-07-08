package meow_be.chat.service;

import lombok.RequiredArgsConstructor;
import meow_be.chat.dto.ChatParticipantCountDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ParticipantNotifier {

    private final SimpMessagingTemplate messagingTemplate;
    public ParticipantNotifier(@Lazy SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyCount(int chatroomId, int count) {
        ChatParticipantCountDto payload = new ChatParticipantCountDto(chatroomId, count);
        messagingTemplate.convertAndSend("/sub/chatroom." + chatroomId + ".participants", payload);
    }
}
