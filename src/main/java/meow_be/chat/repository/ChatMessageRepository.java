package meow_be.chat.repository;

import meow_be.chat.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    Page<ChatMessage> findByChatroomIdOrderByCreatedAtAsc(Integer chatroomId, Pageable pageable);
}
