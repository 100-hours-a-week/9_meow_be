package meow_be.login.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import meow_be.login.security.TokenProvider;
import meow_be.users.domain.User;
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
        Long kakaoId = getKakaoUserId(accessTokenFromKakao);

        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("kakaoId", kakaoId, "isMember", false));
        }


        return ResponseEntity.ok()
                .body(Map.of("kakaoId", kakaoId, "isMember", true));
    }

    @Override
    public ResponseEntity<?> loginWithKakaoId(Long kakaoId) {
        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "가입된 유저가 없습니다."));
        }

        User user = optionalUser.get();

        // Access Token과 Refresh Token을 생성
        String accessToken = tokenProvider.createAccessToken(user.getId());
        String refreshToken = tokenProvider.createRefreshToken(user.getId());

        // Refresh 토큰을 HttpOnly 쿠키로 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)  // HTTPS 환경에서만 전송
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7일
                .build();

        // Access Token을 응답 본문에 포함
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(Map.of(
                        "accessToken", accessToken,
                        "userId", user.getId(),
                        "nickname", user.getNickname()
                ));
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
