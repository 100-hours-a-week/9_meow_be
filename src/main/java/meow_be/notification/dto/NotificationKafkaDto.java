package meow_be.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationKafkaDto {
    private Integer receiverUserId;
    private String content;
    private String type;
    private Integer senderId;
}
