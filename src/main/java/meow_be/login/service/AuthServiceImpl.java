package meow_be.login.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import meow_be.login.security.TokenProvider;
import meow_be.users.domain.Token;
import meow_be.users.domain.User;
import meow_be.users.repository.TokenRepository;
import meow_be.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    public ResponseEntity<?> kakaoLogin(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUri, request, String.class);

        JsonNode tokenJson;
        try {
            tokenJson = objectMapper.readTree(tokenResponse.getBody());
        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 응답 파싱 실패", e);
        }

        String accessTokenFromKakao = tokenJson.get("access_token").asText();
        String refreshTokenFromKakao = tokenJson.get("refresh_token").asText();

        Long kakaoId = getKakaoUserId(accessTokenFromKakao);

        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("kakaoId", kakaoId));
        }

        User user = optionalUser.get();

        String ourJwtAccessToken = tokenProvider.createToken(user.getId());
        tokenRepository.findByUser(user)
                .ifPresentOrElse(
                        existingToken -> {
                            existingToken = Token.builder()
                                    .id(existingToken.getId())
                                    .user(user)
                                    .accessToken(accessTokenFromKakao)
                                    .refreshToken(refreshTokenFromKakao)
                                    .build();
                            tokenRepository.save(existingToken);
                        },
                        () -> {
                            Token newToken = Token.builder()
                                    .user(user)
                                    .accessToken(accessTokenFromKakao)
                                    .refreshToken(refreshTokenFromKakao)
                                    .build();
                            tokenRepository.save(newToken);
                        }
                );

        return ResponseEntity.ok(Map.of("accessToken", ourJwtAccessToken));
    }

    private Long getKakaoUserId(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);  // Authorization: Bearer {access_token}
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUri,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("id").asLong();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("카카오 사용자 정보 요청 실패: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 파싱 실패", e);
        }
    }
}
