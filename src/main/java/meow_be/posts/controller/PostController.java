package meow_be.posts.controller;

import lombok.RequiredArgsConstructor;
import meow_be.posts.dto.PostDto;
import meow_be.posts.service.PostService;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<PagedModel<PostDto>> getPosts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostDto> postDtoPage = postService.getPosts(pageable);

        // PagedModel로 변환
        PagedModel<PostDto> pagedModel = PagedModel.of(
                postDtoPage.getContent(),  // PostDto 리스트
                new PagedModel.PageMetadata(
                        (long) postDtoPage.getSize(),   // size를 long으로 캐스팅
                        (long) postDtoPage.getNumber(), // number를 long으로 캐스팅
                        postDtoPage.getTotalElements(), // totalElements는 이미 long 타입
                        (long) Math.ceil((double) postDtoPage.getTotalElements() / size) // totalPages 계산
                )
        );

        return ResponseEntity.status(HttpStatus.OK).body(pagedModel);
    }
}
