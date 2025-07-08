package meow_be.notification.service;

import lombok.RequiredArgsConstructor;
import meow_be.notification.domain.Notification;
import meow_be.notification.dto.NotificationResponseDto;
import meow_be.notification.repository.NotificationRepository;
import meow_be.notification.sse.SseEmitterManager;
import meow_be.posts.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterManager sseEmitterManager;

    public PageResponse<NotificationResponseDto> getUserNotifications(Integer userId, Pageable pageable) {
        Page<Notification> page = notificationRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, pageable);

        return new PageResponse<>(
                page.map(NotificationResponseDto::fromEntity).getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.isLast()
        );
    }

    public void notify(Integer receiverUserId, Notification notification) {
        notificationRepository.save(notification);
        sseEmitterManager.sendNotification(receiverUserId, NotificationResponseDto.fromEntity(notification));
    }
}
