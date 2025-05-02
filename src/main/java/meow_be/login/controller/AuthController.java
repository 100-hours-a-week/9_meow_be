package meow_be.login.controller;

import lombok.RequiredArgsConstructor;
import meow_be.login.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final AuthService authService;


    @GetMapping("/url")
    public ResponseEntity<String> getKakaoLoginUrl() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri;

        return ResponseEntity.ok(kakaoAuthUrl);
    }
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        return authService.kakaoLogin(code);
    }

}
