/*
package meow_be.notification.kafka;

import lombok.RequiredArgsConstructor;
import meow_be.notification.domain.Notification;
import meow_be.notification.dto.NotificationKafkaDto;
import meow_be.notification.service.NotificationService;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @KafkaListener(topics = "notification", groupId = "notification-group")
    public void consume(NotificationKafkaDto dto) {
        User user = userRepository.findById(dto.getReceiverUserId())
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));

        Notification notification = Notification.builder()
                .user(user)
                .content(dto.getContent())
                .type(dto.getType())
                .senderId(dto.getSenderId())
                .isRead(false)
                .isDeleted(false)
                .build();


        notificationService.notify(dto.getReceiverUserId(), notification);
    }
}
*/
