package meow_be.posts.controller;

import lombok.RequiredArgsConstructor;
import meow_be.posts.dto.PostDto;
import meow_be.posts.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostDto>> getPosts(Pageable pageable) {
        Page<PostDto> posts = postService.getPosts(pageable);
        return ResponseEntity.ok(posts);
    }
}
