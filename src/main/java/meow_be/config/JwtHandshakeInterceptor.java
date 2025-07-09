package meow_be.config;

import lombok.RequiredArgsConstructor;
import meow_be.chat.controller.ChatRoomParticipantManager;
import meow_be.chat.service.ParticipantNotifier;
import meow_be.login.security.TokenProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements ChannelInterceptor {

    private final TokenProvider tokenProvider;
    private final ChatRoomParticipantManager participantManager;
    private final ParticipantNotifier participantNotifier;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            List<String> authHeader = accessor.getNativeHeader("Authorization");

            if (authHeader != null && !authHeader.isEmpty()) {
                String token = authHeader.get(0).replace("Bearer ", "").trim();
                if (StringUtils.hasText(token)) {
                    try {
                        Integer userId = tokenProvider.getUserIdFromToken(token);
                        if (!tokenProvider.validateToken(token, userId)) {
                            throw new IllegalArgumentException("JWT 토큰이 유효하지 않음");
                        }

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        String.valueOf(userId),
                                        null,
                                        List.of()
                                );
                        accessor.setUser(authentication);

                        Integer chatroomId = 1;
                        String sessionId = accessor.getSessionId();
                        boolean joined = participantManager.tryJoin(chatroomId, sessionId);

                        if (!joined) {
                            throw new ChatRoomFullException("채팅방 최대 인원 초과 (20명)");
                        }

                        int count = participantManager.getParticipantCount(chatroomId);
                        participantNotifier.notifyCount(chatroomId,count);

                    } catch (Exception e) {
                        throw new IllegalArgumentException("웹소켓 인증 실패: " + e.getMessage());
                    }
                }
            }
        }

        return message;
    }

    public static class ChatRoomFullException extends RuntimeException {
        public ChatRoomFullException(String message) {
            super(message);
        }
    }
}
