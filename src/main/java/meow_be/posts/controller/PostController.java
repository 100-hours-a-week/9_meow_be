package meow_be.posts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.common.ApiResponse;
import meow_be.login.security.TokenProvider;
import meow_be.posts.dto.PageResponse;
import meow_be.posts.dto.PostDto;
import meow_be.posts.dto.PostEditInfoDto;
import meow_be.posts.dto.PostSummaryDto;
import meow_be.posts.service.PostLikeService;
import meow_be.posts.service.PostService;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name="게시글 컨트롤러",description = "게시글 작성,조회 엔드포인트")
public class PostController {

    private final PostService postService;
    private final AiContentClient aiContentClient;
    private final TokenProvider tokenProvider;
    private final PostLikeService postLikeService;
    private final UserRepository userRepository;


    @GetMapping
    @ResponseBody
    @Operation(summary = "게시글 전체 조회 또는 postType으로 필터링된 게시글 조회")
    public ResponseEntity<PageResponse<PostSummaryDto>> getPosts(
            @RequestParam(value = "postType", required = false) String postType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletRequest request) {

        String token = tokenProvider.extractTokenFromHeader(request);
        Integer userId = null;
        if (token != null) {
            try {
                userId = tokenProvider.getUserIdFromToken(token);
            } catch (Exception e) {
                // 예외 무시 or 로깅
            }
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<PostSummaryDto> postPage = postService.getPostSummaryPage(postType, pageable, userId);

        PageResponse<PostSummaryDto> response = new PageResponse<>(
                postPage.getContent(),
                postPage.getNumber(),
                postPage.getTotalPages(),
                postPage.getTotalElements(),
                postPage.getSize(),
                postPage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회")
    @ResponseBody
    public ResponseEntity<PostDto> getPostById(@PathVariable("postId") int postId,HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromHeader(request);
        Integer userId = null;
        if (token != null) {
            try {
                userId = tokenProvider.getUserIdFromToken(token);
            } catch (Exception e) {
            }
        }
        PostDto postDto = postService.getPostById(postId,userId);
        return ResponseEntity.ok(postDto);
    }
    @PostMapping
    @ResponseBody
    @Operation(summary = "게시글 생성")
    public ResponseEntity<ApiResponse<Integer>> createPost(
            @RequestParam("content") String content,
            @RequestParam("emotion") String emotion,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request) {

        Integer userId = getAuthenticatedUserId(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String postType = user.getAnimalType();

        String transformedContent = aiContentClient.transformpostContent(content, emotion, postType);
        int postId = postService.createPost(content, emotion, postType, images, transformedContent, userId);

        return ResponseEntity.ok(ApiResponse.success(postId, "게시글이 성공적으로 생성되었습니다."));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "특정 유저의 게시글 조회")
    public ResponseEntity<PageResponse<PostSummaryDto>> getUserPosts(
            @PathVariable("userId") Integer userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostSummaryDto> postPage = postService.getUserPostSummaryPage(userId, pageable);

        PageResponse<PostSummaryDto> response = new PageResponse<>(
                postPage.getContent(),
                postPage.getNumber(),
                postPage.getTotalPages(),
                postPage.getTotalElements(),
                postPage.getSize(),
                postPage.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/likes")
    @Operation(summary = "게시글 좋아요")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> likePost(@PathVariable("postId") int postId,
                                                        @RequestBody Map<String, Boolean> requestBody,
                                                        HttpServletRequest request) {

        Integer userId = getAuthenticatedUserId(request);

        Boolean isLiked = requestBody.get("is_liked");
        if (isLiked == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, "Missing 'is_liked' field in request"));
        }

        postLikeService.toggleLike(postId, userId, isLiked);

        return ResponseEntity.ok(ApiResponse.success("success", "좋아요 상태가 변경되었습니다."));
    }

    private Integer getAuthenticatedUserId(HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromHeader(request);
        if (token == null) {
            throw new UnauthorizedException("token not provided");
        }

        Integer userId = tokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            throw new UnauthorizedException("not valid token.");
        }

        return userId;
    }
    @GetMapping("/{postId}/edit")
    @Operation(summary = "게시글 수정 정보 조회")
    @ResponseBody
    public ResponseEntity<?> getPostEditInfo(@PathVariable("postId") int postId,
                                             HttpServletRequest request) {
        try {
            PostEditInfoDto editInfo = postService.getPostEditInfo(postId);
            return ResponseEntity.ok(editInfo);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "unauthorized", "data", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "internal_server_error", "data", null));
        }
    }
    @PutMapping("/{postId}")
    @ResponseBody
    @Operation(summary = "게시글 수정")
    public ResponseEntity<?> editPost(@PathVariable("postId") int postId,
                                      @RequestParam("content") String content,
                                      @RequestParam("emotion") String emotion,
                                      @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                      HttpServletRequest request) {

        Integer userId = getAuthenticatedUserId(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String postType = user.getAnimalType();

        String transformedContent = aiContentClient.transformpostContent(content, emotion, postType);
        postService.editPost(postId, content, emotion, postType, images, transformedContent, userId);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("post_id", postId);
        return ResponseEntity.ok(responseBody);
    }


    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}