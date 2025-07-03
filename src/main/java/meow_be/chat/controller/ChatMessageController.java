package meow_be.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.chat.dto.ChatMessageDto;
import meow_be.chat.dto.ChatMessageRequest;
import meow_be.chat.service.ChatMessageService;
import meow_be.posts.controller.AiContentClient;
import meow_be.posts.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

   /* private final ChatKafkaProducer chatKafkaProducer;*/
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final AiContentClient aiContentClient;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest messageRequest, Principal principal) {
        try {
            String userId = principal.getName();

            String transformedMessage = aiContentClient.transformChatMessage(
                    messageRequest.getMessage(),
                    messageRequest.getAnimalType()
            );

            ChatMessageRequest updatedRequest = ChatMessageRequest.builder()
                    .chatroomId(messageRequest.getChatroomId())
                    .senderId(Integer.parseInt(userId))
                    .animalType(messageRequest.getAnimalType())
                    .message(transformedMessage)
                    .build();
            chatMessageService.saveMessage(messageRequest);

            messagingTemplate.convertAndSend(
                    "/sub/chatroom." + updatedRequest.getChatroomId(),
                    updatedRequest
            );
        } catch (Exception e) {
            log.error("채팅 처리 중 오류 발생", e);
        }
    }
    @GetMapping("/chat/{chatroomId}")
    public PageResponse<ChatMessageDto> getChatMessages(
            @PathVariable Integer chatroomId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return chatMessageService.getMessages(chatroomId, pageable);
    }

}