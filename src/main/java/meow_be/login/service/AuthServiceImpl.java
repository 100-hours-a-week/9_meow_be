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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    public ResponseEntity<?> kakaoLogin(String code,String redirectUri) {
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
            return ResponseEntity.ok()
                    .body(Map.of("kakaoId", kakaoId, "isMember", false));
        }

        return ResponseEntity.ok()
                .body(Map.of("kakaoId", kakaoId, "isMember", true));
    }

    @Override
    @Transactional
    public ResponseEntity<?> loginWithKakaoId(Long kakaoId) {
        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "가입된 유저가 없습니다."));
        }

        User user = optionalUser.get();
        Optional<Token> existingTokenOpt = tokenRepository.findByUserId(user.getId());

        String refreshToken = tokenProvider.createRefreshToken(user.getId());
        String accessToken = tokenProvider.createAccessToken(user.getId());
        if (existingTokenOpt.isPresent()) {
            Token existingToken = existingTokenOpt.get();
            existingToken.setAccessToken(accessToken);
            existingToken.setRefreshToken(refreshToken);
            tokenRepository.save(existingToken);
        } else {
            Token newToken = Token.builder()
                    .userId(user.getId())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            tokenRepository.save(newToken);
        }


        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(Map.of("accessToken", accessToken));
    }

    @Override
    public ResponseEntity<?> refreshTokenFromCookie(String refreshToken) {
        Optional<Token> tokenOptional = tokenRepository.findByRefreshToken(refreshToken);

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "유효하지 않은 Refresh Token"));
        }

        Token token = tokenOptional.get();
        String newAccessToken = tokenProvider.createAccessToken(token.getUserId());

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    private Long getKakaoUserId(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
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
