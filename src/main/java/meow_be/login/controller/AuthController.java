package meow_be.login.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.login.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
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
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        return authService.kakaoLogin(code);
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginWithKakaoId(@RequestBody Map<String, Object> payload) {
        Long kakaoId = ((Number) payload.get("kakaoId")).longValue();
        return authService.loginWithKakaoId(kakaoId);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");
        return authService.refreshToken(refreshToken);
    }
}
