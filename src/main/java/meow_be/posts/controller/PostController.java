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
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostSummaryDto> postPage = postService.getPostSummaryPage(pageable);

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
    public ResponseEntity<PostDto> getPostById(@PathVariable("postId") int postId) {
        PostDto postDto = postService.getPostById(postId);
        return ResponseEntity.ok(postDto);
    }
    @PostMapping
    @ResponseBody
    public ResponseEntity<Integer> createPost(
            @RequestParam("content") String content,
            @RequestParam("emotion") String emotion,
            @RequestParam("post_type") String postType,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        String transformedContent = aiContentClient.transformContent(content, emotion, postType);
        int postId = postService.createPost(content, emotion, postType, images, transformedContent);

        return ResponseEntity.ok(postId);
    }
    @PostMapping("/{postId}/likes")
    @ResponseBody
    public ResponseEntity<String> likePost(@PathVariable("postId") int postId,
                                           @RequestBody Map<String, Boolean> requestBody,
                                           HttpServletRequest request) {

        String token = tokenProvider.extractTokenFromHeader(request);
        if (token == null) {
            return ResponseEntity.status(401).body("token not provided");
        }

        Integer userId = tokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401).body("not valid token.");
        }

        Boolean isLiked = requestBody.get("is_liked");
        if (isLiked == null) {
            return ResponseEntity.badRequest().body("Missing 'is_liked' field in request body.");
        }

        postLikeService.toggleLike(postId, userId, isLiked);

        return ResponseEntity.ok("success");
    }




}