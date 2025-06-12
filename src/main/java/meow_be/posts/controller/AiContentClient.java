package meow_be.posts.controller;

import meow_be.common.AiContentResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AiContentClient {

    private final WebClient webClient;

    public AiContentClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://34.64.213.48:8000").build();
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
}
