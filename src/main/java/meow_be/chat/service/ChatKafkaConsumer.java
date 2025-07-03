/*
package meow_be.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.chat.dto.ChatMessageDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatKafkaConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "chat.from-ai", groupId = "chat-consumer-group")
    public void consume(ChatMessageDto message) {
    }
}

*/
