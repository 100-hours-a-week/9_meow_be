package meow_be.notification.repository;

import meow_be.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Integer userId, Pageable pageable);
}
