package meow_be.chat.service;

import lombok.RequiredArgsConstructor;
import meow_be.chat.domain.ChatMessage;
import meow_be.chat.dto.ChatMessageDto;
import meow_be.chat.dto.ChatMessageRequest;
import meow_be.chat.repository.ChatMessageRepository;
import meow_be.posts.dto.PageResponse;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public void saveMessage(ChatMessageRequest request) {
        User user = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        ChatMessage message = ChatMessage.builder()
                .chatroomId(request.getChatroomId())
                .user(user)
                .animalType(request.getAnimalType())
                .content(request.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);
    }
    public PageResponse<ChatMessageDto> getMessages(Integer chatroomId, Pageable pageable) {
        Page<ChatMessage> page = chatMessageRepository.findByChatroomIdOrderByCreatedAtAsc(chatroomId, pageable);

        List<ChatMessageDto> dtoList = page.getContent().stream()
                .map(chatMessage -> ChatMessageDto.builder()
                        .chatroomId(chatMessage.getChatroomId())
                        .senderId(chatMessage.getUser().getId())
                        .animalType(chatMessage.getAnimalType())
                        .message(chatMessage.getContent())
                        .timestamp(chatMessage.getCreatedAt())
                        .build())
                .toList();

        return new PageResponse<>(
                dtoList,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.isLast()
        );
    }
}
