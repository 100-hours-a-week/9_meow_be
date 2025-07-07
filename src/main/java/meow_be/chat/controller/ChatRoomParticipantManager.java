package meow_be.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomParticipantManager {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_PARTICIPANTS = 20;

    public boolean tryJoin(Integer chatroomId, String sessionId) {
        String key = getKey(chatroomId);
        Long size = redisTemplate.opsForSet().size(key);
        if (size != null && size >= MAX_PARTICIPANTS) {
            return false;
        }
        redisTemplate.opsForSet().add(key, sessionId);
        return true;
    }

    public void leave(Integer chatroomId, String sessionId) {
        redisTemplate.opsForSet().remove(getKey(chatroomId), sessionId);
    }

    private String getKey(Integer chatroomId) {
        return "chatroom:participants:" + chatroomId;
    }
}
