package meow_be.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.login.security.TokenProvider;
import meow_be.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "회원가입")
    public ResponseEntity<Long> createUser(
            @RequestParam("kakaoId") Long kakaoId,
            @RequestParam("nickname") String nickname,
            @RequestParam("animalType") String animalType,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        int userId = userService.createUser(kakaoId,nickname, animalType,profileImage);
        return ResponseEntity.ok(kakaoId);
    }
    @GetMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 확인")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
        boolean isDuplicate = userService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(isDuplicate);
    }
    @GetMapping("/profileimage")
    @Operation(summary = "유저 프로필 이미지 조회")
    public ResponseEntity<?> getUserProfileImage(HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromHeader(request);
        if (token == null) {
            return ResponseEntity.status(401).body("token not provided");
        }

        Integer userId = tokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401).body("not valid token.");
        }

        String profileImageUrl = userService.getProfileImageUrlByUserId(userId);
        return ResponseEntity.ok().body(Map.of("profileImageUrl", profileImageUrl));
    }


}
