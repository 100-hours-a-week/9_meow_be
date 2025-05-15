package meow_be.login.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> kakaoLogin(String code, String redirectUri);

    ResponseEntity<?> loginWithKakaoId(Long kakaoId, HttpServletRequest request);

    ResponseEntity<?> refreshTokenFromCookie(String refreshToken);
}
