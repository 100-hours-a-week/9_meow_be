package meow_be.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.chat.dto.ChatMessageDto;
import meow_be.chat.dto.ChatMessageRequest;
import meow_be.chat.service.ChatMessageService;
import meow_be.posts.controller.AiContentClient;
import meow_be.posts.dto.PageResponse;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {

   /* private final ChatKafkaProducer chatKafkaProducer;*/
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final AiContentClient aiContentClient;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest messageRequest,  Principal principal) {
        try {
            log.info("[WS] @MessageMapping principal = {}", principal);
            if (principal == null) {
                throw new IllegalStateException("WebSocket 인증 정보 없음");
            }

            User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
            Integer userId = user.getId();
            String transformedMessage = aiContentClient.transformChatMessage(
                    messageRequest.getMessage(),
                    messageRequest.getAnimalType()
            );

            ChatMessageRequest saveRequest = ChatMessageRequest.builder()
                    .chatroomId(messageRequest.getChatroomId())
                    .senderId(userId)
                    .animalType(messageRequest.getAnimalType())
                    .message(transformedMessage)
                    .build();

            chatMessageService.saveMessage(saveRequest);

            String senderNickname = user.getNickname();
            String senderProfileImage = user.getProfileImageUrl();

            ChatMessageDto messageDto = ChatMessageDto.builder()
                    .chatroomId(saveRequest.getChatroomId())
                    .senderId(userId)
                    .senderNickname(senderNickname)
                    .senderProfileImage(senderProfileImage)
                    .animalType(saveRequest.getAnimalType())
                    .message(saveRequest.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            log.info("메시지 전송 → {}", messageDto);

            messagingTemplate.convertAndSend(
                    "/sub/chatroom." + messageDto.getChatroomId(),
                    messageDto
            );
        } catch (Exception e) {
            log.error("채팅 처리 중 오류 발생", e);
        }
    }



    @GetMapping("/chat/{chatroomId}")
    public PageResponse<ChatMessageDto> getChatMessages(
            @PathVariable Integer chatroomId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        return chatMessageService.getMessages(chatroomId, pageable);
    }


}