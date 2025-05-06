package meow_be.login.service;

import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> kakaoLogin(String code);
    ResponseEntity<?> loginWithKakaoId(Long kakaoId);

}
