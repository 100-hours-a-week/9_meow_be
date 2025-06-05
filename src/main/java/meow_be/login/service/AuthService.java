package meow_be.login.service;

import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> kakaoLogin(String code, String redirectUri);

    ResponseEntity<?> loginWithKakaoId(Long kakaoId);

    ResponseEntity<?> refreshTokenFromCookie(String refreshToken);
    ResponseEntity<?> logout(Integer userId);

}
