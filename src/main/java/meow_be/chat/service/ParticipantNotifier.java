package meow_be.chat.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import meow_be.chat.dto.ChatParticipantEventDto;


@Component
public class ParticipantNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public ParticipantNotifier(@Lazy SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyJoin(int chatroomId, int count, String nickname) {
        String msg = nickname + "님이 입장하였습니다.";
        ChatParticipantEventDto payload = new ChatParticipantEventDto(chatroomId, count, msg);
        messagingTemplate.convertAndSend("/sub/chatroom." + chatroomId + ".participants", payload);
    }

    public void notifyLeave(int chatroomId, int count, String nickname) {
        String msg = nickname + "님이 퇴장하였습니다.";
        ChatParticipantEventDto payload = new ChatParticipantEventDto(chatroomId, count, msg);
        messagingTemplate.convertAndSend("/sub/chatroom." + chatroomId + ".participants", payload);
    }
}

