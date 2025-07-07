package meow_be.config;

import lombok.RequiredArgsConstructor;
import meow_be.chat.controller.ChatRoomParticipantManager;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final ChatRoomParticipantManager participantManager;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        Integer chatroomId = 1;
        participantManager.leave(chatroomId, sessionId);
    }
}
