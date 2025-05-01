package meow_be.posts.controller;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Component
public class AiContentClient {

    private final WebClient webClient;

    public AiContentClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ai-server-url").build();  // 실제 AI 서버 URL로 변경
    }

    public String transformContent(String originalContent) {
        try {
            String aiResponse = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/generate/post")
                            .queryParam("content", originalContent)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (aiResponse != null && !aiResponse.isEmpty()) {
                return aiResponse;
            } else {
                throw new RuntimeException("AI 서버 응답 없음");
            }
        } catch (Exception e) {
            throw new RuntimeException("AI 서버 요청 실패: " + e.getMessage());
        }
    }
}
