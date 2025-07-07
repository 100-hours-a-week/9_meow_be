package meow_be.config;

import lombok.RequiredArgsConstructor;
import meow_be.chat.controller.ChatRoomParticipantManager;
import meow_be.chat.dto.ChatParticipantCountDto;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final ChatRoomParticipantManager participantManager;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        Integer chatroomId = 1;
        participantManager.leave(chatroomId, sessionId);

        int count = participantManager.getParticipantCount(chatroomId);
        ChatParticipantCountDto payload = new ChatParticipantCountDto(chatroomId, count);
        messagingTemplate.convertAndSend("/sub/chatroom." + chatroomId + ".participants", payload);
    }
}
