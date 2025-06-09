package meow_be.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import meow_be.common.ApiResponse;
import meow_be.login.security.TokenProvider;
import meow_be.posts.dto.PageResponse;
import meow_be.users.dto.FollowUserDto;
import meow_be.users.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name="팔로우 컨트롤러",description = "팔로우하기, 팔로우 팔로잉 목록 조회")
public class FollowController {

    private final FollowService followService;
    private final TokenProvider tokenProvider;

    @PostMapping("/follow/{userId}")
    @Operation(summary = "특정 유저 팔로우")
    public ResponseEntity<?> follow(@PathVariable int userId, HttpServletRequest request) {
        Integer currentUserId = getUserIdFromRequest(request);
        followService.followUser(currentUserId, userId);
        return ResponseEntity.ok(ApiResponse.success("팔로우 완료"));
    }

    @DeleteMapping("/follow/{userId}")
    @Operation(summary = "특정 유저 팔로우 취소")
    public ResponseEntity<?> unfollow(@PathVariable int userId, HttpServletRequest request) {
        Integer currentUserId = getUserIdFromRequest(request);
        followService.unfollowUser(currentUserId, userId);
        return ResponseEntity.ok(ApiResponse.success("언팔로우 완료"));
    }
    @GetMapping("/{userId}/followings")
    @Operation(summary = "유저 팔로잉 목록 조회")
    public ResponseEntity<?> getFollowings(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<FollowUserDto> response = followService.getFollowings(userId, page, size);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{userId}/followers")
    @Operation(summary = "유저 팔로워 목록 조회")
    public ResponseEntity<?> getFollowers(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<FollowUserDto> response = followService.getFollowers(userId, page, size);
        return ResponseEntity.ok(response);
    }


    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromHeader(request);
        if (token == null) throw new IllegalArgumentException("Authorization 헤더가 유효하지 않습니다.");
        return tokenProvider.getUserIdFromToken(token);
    }
}
