package meow_be.login.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.common.ApiResponse;
import meow_be.login.security.TokenProvider;
import meow_be.login.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name="로그인 관련 컨트롤러",description = "카카오 로그인, 인증, 자체로그인")
public class AuthController {

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final AuthService authService;
    private final TokenProvider tokenProvider;

    @GetMapping("/url")
    @Operation(summary = "카카오 로그인 url 요청")
    public ResponseEntity<String> getKakaoLoginUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String redirectUri;
        if (referer != null && referer.contains("localhost:5173")) {
            redirectUri = "http://localhost:5173/redirect";
        } else if (referer.contains("3.39.3.208")) {
            redirectUri = "http://3.39.3.208/redirect";}
        else if (referer.contains("testdev")) {
            redirectUri = "http://testdev.meowng.com/redirect";}
        else {
            redirectUri = kakaoRedirectUri;
        }

        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=" + kakaoClientId +
                "&redirect_uri=" + redirectUri;
        return ResponseEntity.ok(kakaoAuthUrl);
    }

    @GetMapping("/kakao/callback")
    @Operation(summary = "카카오 인증")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code,
                                           HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String redirectUri;

        if (referer != null && referer.contains("localhost:5173")) {
            redirectUri = "http://localhost:5173/redirect";
        } else if (referer != null && referer.contains("3.39.3.208")) {
            redirectUri = "http://3.39.3.208/redirect";
        } else if (referer != null && referer.contains("testdev.meowng.com")) {
            redirectUri = "http://testdev.meowng.com/redirect";
        }
        else {
            redirectUri = kakaoRedirectUri;
        }

        return authService.kakaoLogin(code, redirectUri);
    }
    @PostMapping("/login")
    @Operation(summary = "kakaoId 자체 로그인",requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "카카오 ID 요청 예시",
            required = true,
            content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "kakaoId 예시", value = "{ \"kakaoId\": 4242621681 }"))))
    public ResponseEntity<?> loginWithKakaoId(@RequestBody Map<String, Object> payload) {
        Long kakaoId = ((Number) payload.get("kakaoId")).longValue();
        return authService.loginWithKakaoId(kakaoId);
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh Token이 쿠키에 없습니다."));
        }

        return authService.refreshTokenFromCookie(refreshToken);
    }
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "Authorization 헤더에서 accessToken을 받아 서버 저장소에서 access/refresh 토큰을 삭제합니다.")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromHeader(request);
        if (token == null || token.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Authorization 헤더에 토큰이 없습니다."));
        }

        try {
            int userId = tokenProvider.getUserIdFromToken(token);
            return authService.logout(userId);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."));
        }

    }
}
