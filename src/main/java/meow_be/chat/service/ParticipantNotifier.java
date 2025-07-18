package meow_be.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import meow_be.chat.dto.ChatParticipantEventDto;


@Component
@Slf4j
public class ParticipantNotifier {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    public ParticipantNotifier(@Lazy SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService= chatMessageService;
    }


    public void notifyJoin(int chatroomId, int count, String nickname, int userId) {
        String msg = nickname + "님이 입장하였습니다.";
        ChatParticipantEventDto payload = new ChatParticipantEventDto(chatroomId, count);

        messagingTemplate.convertAndSend("/sub/chatroom." + chatroomId + ".participants", payload);

        chatMessageService.saveAndSendSystemMessage(chatroomId, "enter", msg, userId);
    }

    public void notifyLeave(int chatroomId, int count, String nickname, int userId) {
        String msg = nickname + "님이 퇴장하였습니다.";
        ChatParticipantEventDto payload = new ChatParticipantEventDto(chatroomId, count);
        messagingTemplate.convertAndSend("/sub/chatroom." + chatroomId + ".participants", payload);
        chatMessageService.saveAndSendSystemMessage(chatroomId, "exit", msg, userId);
    }
}

