package meow_be.posts.controller;

import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.Post;
import meow_be.posts.dto.PostDto;
import meow_be.posts.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    // 게시물 전체 조회
    @GetMapping
    public ResponseEntity<?> getPosts(Pageable pageable) {
        Page<PostDto> postDto = postService.getPosts(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(postDto);
    }



}

