package meow_be.posts.controller;

import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.Post;
import meow_be.posts.dto.PostDto;
import meow_be.posts.dto.PostSummaryDto;
import meow_be.posts.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<PostSummaryDto>> getPosts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<PostSummaryDto> responseData = postService.getPostSummaries(pageable);  // 서비스에서 변환 완료된 DTO 반환
        return ResponseEntity.ok(responseData);
    }
    @GetMapping("/{postId}")
    @ResponseBody
    public ResponseEntity<PostDto> getPostById(@PathVariable("postId") int postId) {
        PostDto postDto = postService.getPostById(postId);
        return ResponseEntity.ok(postDto);
    }
    @PostMapping
    @ResponseBody
    public ResponseEntity<String> createPost(
            @RequestParam("content") String content,
            @RequestParam("emotion") String emotion,
            @RequestParam("post_type") String postType,
            @RequestParam("images") List<MultipartFile> images) {

        postService.createPost(content, emotion, postType, images);
        return ResponseEntity.ok("게시물이 성공적으로 생성되었습니다.");
    }

}
