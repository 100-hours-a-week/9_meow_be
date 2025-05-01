package meow_be.posts.controller;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component;

@Component
public class AiContentClient {

    private final WebClient webClient;

    public AiContentClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000://").build();
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
