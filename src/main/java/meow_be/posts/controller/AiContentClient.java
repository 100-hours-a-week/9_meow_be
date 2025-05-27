package meow_be.posts.controller;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AiContentClient {

    private final WebClient webClient;

    public AiContentClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ai:8000").build();
    }

    public String transformContent(String originalContent, String emotion, String post_type) {
        try {
            // JSON body에 담길 데이터를 Map으로 구성
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("content", originalContent);
            requestBody.put("emotion", emotion);
            requestBody.put("post_type", post_type);

            // WebClient를 통해 JSON 바디로 POST 요청 전송
            String aiResponse = webClient.post()
                    .uri("/generate/post")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
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
