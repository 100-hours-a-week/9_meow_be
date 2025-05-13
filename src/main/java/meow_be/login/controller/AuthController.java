package meow_be.login.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name="게시글 컨트롤러",description = "게시글 작성,조회 엔드포인트")
public class AuthController {

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final AuthService authService;

    @GetMapping("/url")
    public ResponseEntity<String> getKakaoLoginUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String redirectUri;
        if (referer != null && referer.contains("localhost:5173")) {
            redirectUri = "http://localhost:5173/redirect";
        } else {
            redirectUri = kakaoRedirectUri;
        }

        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=" + kakaoClientId +
                "&redirect_uri=" + redirectUri;
        log.info(kakaoAuthUrl);
        log.info(kakaoRedirectUri);
        return ResponseEntity.ok(kakaoAuthUrl);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code,
                                           HttpServletRequest request) {
        // referer 기반 redirect uri 추정
        String referer = request.getHeader("Referer");
        String redirectUri;

        if (referer != null && referer.contains("localhost:5173")) {
            redirectUri = "http://localhost:5173/redirect";
        } else {
            redirectUri = kakaoRedirectUri;
        }

        return authService.kakaoLogin(code, redirectUri);  // 수정된 부분
    }
    @PostMapping("/login")
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

}
