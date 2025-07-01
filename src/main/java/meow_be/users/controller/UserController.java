package meow_be.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.login.security.TokenProvider;
import meow_be.users.dto.EditUserProfileRequest;
import meow_be.users.dto.EditUserProfileResponse;
import meow_be.users.dto.UserCreateRequestDto;
import meow_be.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "유저 컨트롤러", description = "회원가입, 닉네임 중복확인, 마이페이지, 회원정보 수정 등")
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping
    @Operation(summary = "회원가입")
    public ResponseEntity<?> createUser(@RequestBody UserCreateRequestDto request) {
        long kakaoId = userService.createUser(request);
        return ResponseEntity.ok(kakaoId);
    }


    @GetMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 확인")
    public ResponseEntity<Boolean> checkNicknameDuplicate(
            @RequestParam("nickname") String nickname,
            HttpServletRequest request
    ) { Integer userId = extractUserIdFromRequest(request);
        boolean isDuplicate = userService.isNicknameDuplicate(nickname, userId);
        return ResponseEntity.ok(isDuplicate);
    }


    @GetMapping("/profileimage")
    @Operation(summary = "유저 프로필 이미지 조회")
    public ResponseEntity<?> getUserProfileImage(HttpServletRequest request) {
        Integer userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("token not provided or invalid");
        }

        try {
            String profileImageUrl = userService.getProfileImageUrlByUserId(userId);
            return ResponseEntity.ok(Map.of("profileImageUrl", profileImageUrl));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("프로필 이미지가 존재하지 않습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }
    }

    @GetMapping("/profile/{userId}")
    @Operation(summary = "회원페이지 사용자 정보 상단 조회1", description = "회원페이지 위쪽에 표시될 회원 정보를 가져옵니다.")
    public ResponseEntity<?> getUserProfile(
            @PathVariable("userId") Integer userId,
            HttpServletRequest request
    ) {
        String token = tokenProvider.extractTokenFromHeader(request);
        Integer loginUserId = (token != null) ? tokenProvider.getUserIdFromToken(token) : null;

        return ResponseEntity.ok(userService.getUserProfile(userId, loginUserId));
    }
    @GetMapping("/my-profile")
    @Operation(summary = "마이페이지 사용자 정보 상단 조회", description = "마이페이지 위쪽에 표시될 회원정보를 가져옵니다.")
    public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
        Integer userId = extractUserIdFromRequest(request);
        return ResponseEntity.ok(Map.of("userId", userId));
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

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 합니다.")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        Integer userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("token not provided or invalid");
        }

        userService.deleteUser(userId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }


    private Integer extractUserIdFromRequest(HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromHeader(request);
        if (token == null) return null;

        return tokenProvider.getUserIdFromToken(token);
    }
}
