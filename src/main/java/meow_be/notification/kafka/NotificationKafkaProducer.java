/*
package meow_be.notification.kafka;

import lombok.RequiredArgsConstructor;
import meow_be.notification.dto.NotificationKafkaDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, NotificationKafkaDto> kafkaTemplate;

    private static final String TOPIC = "notification";

    public void send(NotificationKafkaDto message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
*/
