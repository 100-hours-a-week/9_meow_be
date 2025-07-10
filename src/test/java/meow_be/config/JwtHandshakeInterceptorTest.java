package meow_be.config;

import meow_be.chat.controller.ChatRoomParticipantManager;
import meow_be.chat.service.ParticipantNotifier;
import meow_be.login.security.TokenProvider;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class JwtHandshakeInterceptorTest {

    @InjectMocks
    private JwtHandshakeInterceptor interceptor;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private ChatRoomParticipantManager participantManager;

    @Mock
    private ParticipantNotifier participantNotifier;

    @Mock
    private UserRepository userRepository;

    @Test
    void testPrincipalIsSetWhenValidTokenProvided() {
        // given
        String validToken = "valid.jwt.token";
        Integer userId = 123;
        User mockUser = User.builder().id(userId).nickname("테스트유저").build();

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "Bearer " + validToken);
        accessor.setSessionId("session-1");

        Message<byte[]> message = MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders()
        );

        // mock behavior
        Mockito.when(tokenProvider.getUserIdFromToken(validToken)).thenReturn(userId);
        Mockito.when(tokenProvider.validateToken(validToken, userId)).thenReturn(true);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Mockito.when(participantManager.tryJoin(Mockito.anyInt(), Mockito.anyString())).thenReturn(true);

        // when
        Message<?> result = interceptor.preSend(message, mock(MessageChannel.class));

        // then
        StompHeaderAccessor resultAccessor = StompHeaderAccessor.wrap(result);
        Principal principal = resultAccessor.getUser();

        assertNotNull(principal);
        assertTrue(principal instanceof UsernamePasswordAuthenticationToken);

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) principal;
        assertEquals(mockUser, authentication.getPrincipal());
    }
}
