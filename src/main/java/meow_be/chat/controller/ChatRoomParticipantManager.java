package meow_be.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ChatRoomParticipantManager {

    private final RedisTemplate<String, String> redisTemplate;

    public void join(Integer chatroomId, Integer userId, String sessionId) {
        redisTemplate.opsForSet().add(getSessionKey(chatroomId, userId), sessionId);
        redisTemplate.opsForSet().add(getUserKey(chatroomId), String.valueOf(userId));
    }

    public void leave(Integer chatroomId, Integer userId, String sessionId) {
        String sessionKey = getSessionKey(chatroomId, userId);
        redisTemplate.opsForSet().remove(sessionKey, sessionId);

        Long remainingSessions = redisTemplate.opsForSet().size(sessionKey);
        if (remainingSessions == null || remainingSessions == 0) {
            redisTemplate.delete(sessionKey);
            redisTemplate.opsForSet().remove(getUserKey(chatroomId), String.valueOf(userId));
        }
    }

    public int getParticipantCount(Integer chatroomId) {
        Long count = redisTemplate.opsForSet().size(getUserKey(chatroomId));
        return count != null ? count.intValue() : 0;
    }

    private String getSessionKey(Integer chatroomId, Integer userId) {
        return "chatroom:" + chatroomId + ":user:" + userId + ":sessions";
    }

    private String getUserKey(Integer chatroomId) {
        return "chatroom:" + chatroomId + ":participants";
    }
}
