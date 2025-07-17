package meow_be.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomParticipantManager {

    private final RedisTemplate<String, String> redisTemplate;

    public void join(Integer chatroomId, String sessionId) {
        String key = getKey(chatroomId);
        redisTemplate.opsForSet().add(key, sessionId);
    }

    public int getParticipantCount(Integer chatroomId) {
        Long count = redisTemplate.opsForSet().size(getKey(chatroomId));
        return count != null ? count.intValue() : 0;
    }

    public void leave(Integer chatroomId, String sessionId) {
        redisTemplate.opsForSet().remove(getKey(chatroomId), sessionId);
    }

    private String getKey(Integer chatroomId) {
        return "chatroom:participants:" + chatroomId;
    }
}
