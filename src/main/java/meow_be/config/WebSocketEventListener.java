package meow_be.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.chat.controller.ChatRoomParticipantManager;
import meow_be.chat.service.ParticipantNotifier;
import meow_be.users.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ChatRoomParticipantManager participantManager;
    private final ParticipantNotifier participantNotifier;
    private final UserRepository userRepository;

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/sub/chatroom.")) {
            Integer chatroomId = 1;

            Integer userId = (Integer) accessor.getSessionAttributes().get("userId");

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    int count = participantManager.getParticipantCount(chatroomId);

                    if (userId != null) {
                        String nickname = userRepository.findById(userId)
                                .map(user -> user.getNickname())
                                .orElse("알 수 없는 사용자");
                        participantNotifier.notifyJoin(chatroomId, count, nickname);
                        log.info("구독 발생 - 채팅방 {}, 사용자 {} 입장 알림 전송, {}명이 접속중", chatroomId, nickname,count);
                    } else {
                        log.info("구독 발생 - 채팅방 {} 참여자 수 알림 전송", chatroomId);
                    }
                }
            }, 500);
        }
    }
}
