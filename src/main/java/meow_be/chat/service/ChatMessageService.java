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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void saveMessage(ChatMessageDto request) {
        User user = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        ChatMessage message = ChatMessage.builder()
                .chatroomId(request.getChatroomId())
                .user(user)
                .animalType(request.getAnimalType())
                .content(request.getMessage())
                .type(request.getType())
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);
    }
    public PageResponse<ChatMessageDto> getMessages(Integer chatroomId, Pageable pageable) {
        Page<ChatMessage> page = chatMessageRepository.findByChatroomIdOrderByCreatedAtDesc(chatroomId, pageable);

        List<ChatMessageDto> dtoList = page.getContent().stream()
                .map(chatMessage -> ChatMessageDto.builder()
                        .chatroomId(chatMessage.getChatroomId())
                        .senderId(chatMessage.getUser().getId())
                        .senderNickname(chatMessage.getUser().getNickname())
                        .senderProfileImage(chatMessage.getUser().getProfileImageUrl())
                        .animalType(chatMessage.getAnimalType())
                        .message(chatMessage.getContent())
                        .type(chatMessage.getType())
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
    public void saveAndSendSystemMessage(Integer chatroomId, String type, String content, Integer senderId) {
        User user = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        ChatMessageDto request = ChatMessageDto.builder()
                .chatroomId(chatroomId)
                .message(content)
                .animalType(user.getAnimalType())
                .type(type)
                .senderId(senderId)
                .build();

        saveMessage(request);
        ChatMessageDto messageDto = ChatMessageDto.builder()
                .chatroomId(chatroomId)
                .senderId(senderId)
                .senderNickname(user.getNickname())
                .senderProfileImage(user.getProfileImageUrl())
                .animalType(user.getAnimalType())
                .message(content)
                .type(type)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/sub/chatroom." + chatroomId, messageDto);
    }

}
