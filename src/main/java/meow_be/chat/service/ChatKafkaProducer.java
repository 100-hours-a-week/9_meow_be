/*
package meow_be.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.chat.dto.ChatMessageDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatKafkaProducer {

    private final KafkaTemplate<String, ChatMessageDto> kafkaTemplate;

    private static final String TOPIC = "chat.to-ai";

    public void sendMessage(ChatMessageDto messageDto) {
        kafkaTemplate.send(TOPIC, messageDto);
    }
}*/
