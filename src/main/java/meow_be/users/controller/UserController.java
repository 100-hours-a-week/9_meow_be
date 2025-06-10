package meow_be.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.login.security.TokenProvider;
import meow_be.users.dto.EditUserProfileRequest;
import meow_be.users.dto.EditUserProfileResponse;
import meow_be.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "유저 컨트롤러", description = "회원가입, 닉네임 중복확인, 마이페이지, 회원정보 수정 등")
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
        int userId = userService.createUser(kakaoId, nickname, animalType, profileImage);
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
        Integer userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("token not provided or invalid");
        }

        String profileImageUrl = userService.getProfileImageUrlByUserId(userId);
        return ResponseEntity.ok(Map.of("profileImageUrl", profileImageUrl));
    }
    @GetMapping("/profile/{userId}")
    @Operation(summary = "마이페이지 사용자 정보 상단 조회", description = "마이페이지 위쪽에 표시될 회원 정보를 가져옵니다.")
    public ResponseEntity<?> getUserProfile(
            @PathVariable("userId") Integer userId,
            HttpServletRequest request
    ) {
        String token = tokenProvider.extractTokenFromHeader(request);
        Integer loginUserId = (token != null) ? tokenProvider.getUserIdFromToken(token) : null;

        return ResponseEntity.ok(userService.getUserProfile(userId, loginUserId));
    }

    @GetMapping("/edit-profile")
    @Operation(summary = "회원정보 수정 페이지 유저 정보 조회", description = "회원정보 수정 페이지에 표시될 닉네임과 프로필 이미지를 불러옵니다.")
    public ResponseEntity<EditUserProfileResponse> getEditUserProfile(HttpServletRequest request) {
        Integer userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }

        EditUserProfileResponse userInfo = userService.getEditUserProfile(userId);
        return ResponseEntity.ok(userInfo);
    }
    @PatchMapping
    @Operation(summary = "회원정보 수정", description = "회원 정보를 수정합니다.")
    public ResponseEntity<?> updateUserProfile(@RequestBody EditUserProfileRequest requestBody, HttpServletRequest request) {
        Integer userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("token not provided or invalid");
        }

        userService.updateUserProfile(userId, requestBody);
        return ResponseEntity.ok().build();
    }

    private Integer extractUserIdFromRequest(HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromHeader(request);
        if (token == null) return null;

        return tokenProvider.getUserIdFromToken(token);
    }
}
