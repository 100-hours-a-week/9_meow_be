package meow_be.posts.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.login.security.TokenProvider;
import meow_be.posts.dto.PageResponse;
import meow_be.posts.dto.PostDto;
import meow_be.posts.dto.PostSummaryDto;
import meow_be.posts.service.PostLikeService;
import meow_be.posts.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final AiContentClient aiContentClient;
    private final TokenProvider tokenProvider;
    private final PostLikeService postLikeService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<PageResponse<PostSummaryDto>> getPosts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromHeader(request);
        Integer userId = null;
        if (token != null) {
            try {
                userId = tokenProvider.getUserIdFromToken(token);
            } catch (Exception e) {
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostSummaryDto> postPage = postService.getPostSummaryPage(pageable,userId);

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
    public ResponseEntity<?> createPost(
            @RequestParam("content") String content,
            @RequestParam("emotion") String emotion,
            @RequestParam("post_type") String postType,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request) {

        Integer userId = getAuthenticatedUserId(request);
        
        String transformedContent = aiContentClient.transformContent(content, emotion, postType);
        int postId = postService.createPost(content, emotion, postType, images, transformedContent,userId);

        return ResponseEntity.ok(postId);
    }
    @PostMapping("/{postId}/likes")
    @ResponseBody
    public ResponseEntity<String> likePost(@PathVariable("postId") int postId,
                                           @RequestBody Map<String, Boolean> requestBody,
                                           HttpServletRequest request) {

        Integer userId = getAuthenticatedUserId(request);

        Boolean isLiked = requestBody.get("is_liked");
        if (isLiked == null) {
            return ResponseEntity.badRequest().body("Missing 'is_liked' field in request");
        }

        postLikeService.toggleLike(postId, userId, isLiked);

        return ResponseEntity.ok("success");
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
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }






}