package meow_be.posts.controller;

import lombok.extern.slf4j.Slf4j;
import meow_be.common.AiContentResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AiContentClient {

    private final WebClient webClient;

    public AiContentClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://aiserver.meowng.com:8000").build();
    }

    public String transformpostContent(String originalContent, String emotion, String post_type) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("content", originalContent);
            requestBody.put("emotion", emotion);
            requestBody.put("post_type", post_type);

            AiContentResponse aiResponse = webClient.post()
                    .uri("/generate/post")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(AiContentResponse.class)
                    .block();

            if (aiResponse != null && aiResponse.getStatusCode() == 200) {
                return aiResponse.getData();
            } else {
                throw new RuntimeException("AI 서버 오류 응답: " +
                        (aiResponse != null ? aiResponse.getMessage() : "응답 없음"));
            }
        } catch (Exception e) {
            throw new RuntimeException("AI 서버 요청 실패: " + e.getMessage(), e);
        }
    }
    public String transformcommentContent(String originalContent,String post_type) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("content", originalContent);
            requestBody.put("post_type", post_type);
            requestBody.put("emotion", "normal");


            AiContentResponse aiResponse = webClient.post()
                    .uri("/generate/comment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(AiContentResponse.class)
                    .block();

            if (aiResponse != null && aiResponse.getStatusCode() == 200) {
                return aiResponse.getData();
            } else {
                throw new RuntimeException("AI 서버 오류 응답: " +
                        (aiResponse != null ? aiResponse.getMessage() : "응답 없음"));
            }
        } catch (Exception e) {
            throw new RuntimeException("AI 서버 요청 실패: " + e.getMessage(), e);
        }
    }
    public String transformChatMessage(String originalContent, String animalType) {
        try {
            if (originalContent == null || originalContent.isBlank()) {
                throw new IllegalArgumentException("입력 메시지가 비어 있음");
            }

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text", originalContent);
            requestBody.put("post_type", animalType);

            AiContentResponse aiResponse = webClient.post()
                    .uri("/generate/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(AiContentResponse.class)
                    .block();

            if (aiResponse == null) {
                throw new RuntimeException("AI 서버 응답 없음");
            }

            if (aiResponse.getStatusCode() != 200) {
                throw new RuntimeException("AI 서버 오류: " + aiResponse.getMessage());
            }

            String result = aiResponse.getMessage();

            if (result == null || result.isBlank()) {
                throw new RuntimeException("AI가 빈 메시지를 반환함");
            }

            return result;

        } catch (Exception e) {
            log.error("AI 호출 실패", e);
            throw new RuntimeException("AI 변환 실패: " + e.getMessage(), e);
        }
    }


}
