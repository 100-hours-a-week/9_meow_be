package meow_be.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.chat.controller.ChatRoomParticipantManager;
import meow_be.chat.service.ParticipantNotifier;
import meow_be.users.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final ChatRoomParticipantManager participantManager;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ParticipantNotifier participantNotifier;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Integer userId = (Integer) accessor.getSessionAttributes().get("userId");

        if (userId == null) {
            log.warn("Disconnect 이벤트에서 userId를 찾을 수 없음");
            return;
        }

        Integer chatroomId = 1;
        log.info("세션 연결 해제 감지: userId = {}", userId);

        participantManager.leave(chatroomId, userId);
        log.info("채팅방 {} 에서 사용자 제거 완료", chatroomId);

        int count = participantManager.getParticipantCount(chatroomId);

        String nickname = userRepository.findById(userId)
                .map(user -> user.getNickname())
                .orElse("알 수 없는 사용자");

        participantNotifier.notifyLeave(chatroomId, count, nickname);
    }
}
