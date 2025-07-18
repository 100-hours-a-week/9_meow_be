package meow_be.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.chat.controller.ChatRoomParticipantManager;
import meow_be.chat.service.ParticipantNotifier;
import meow_be.login.security.TokenProvider;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.apache.http.client.methods.RequestBuilder.put;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements ChannelInterceptor {

    private final TokenProvider tokenProvider;
    private final ChatRoomParticipantManager participantManager;
    private final ParticipantNotifier participantNotifier;
    private final UserRepository userRepository;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeader = accessor.getNativeHeader("Authorization");

            if (authHeader != null && !authHeader.isEmpty()) {
                String token = authHeader.get(0).replace("Bearer ", "").trim();
                if (StringUtils.hasText(token)) {
                    try {
                        Integer userId = tokenProvider.getUserIdFromToken(token);
                        if (!tokenProvider.validateToken(token, userId)) {
                            throw new IllegalArgumentException("JWT 토큰이 유효하지 않음");
                        }
                        accessor.getSessionAttributes().put("userId",userId);

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userId, null, List.of());

                        accessor.setUser(auth);
                        Integer chatroomId = 1;
                        participantManager.join(chatroomId, userId,sessionId);
                        log.info("채팅방 {} 참여자 등록: userId = {}", chatroomId, userId);

                        int count = participantManager.getParticipantCount(chatroomId);
                        log.info("현재 채팅방 {} 참여자 수: {}", chatroomId, count);

                        String nickname = userRepository.findById(userId)
                                .map(User::getNickname)
                                .orElse("알 수 없는 사용자");

                        participantNotifier.notifyJoin(chatroomId, count, nickname,userId);
                        return MessageBuilder.withPayload(message.getPayload())
                                .copyHeaders(accessor.getMessageHeaders())
                                .setHeader("simpUser", auth)
                                .build();

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
