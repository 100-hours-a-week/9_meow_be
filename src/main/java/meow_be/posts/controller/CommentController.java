package meow_be.posts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.login.security.TokenProvider;
import meow_be.posts.dto.CommentResponseDto;
import meow_be.posts.dto.PageResponse;
import meow_be.posts.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name="댓글 컨트롤러",description = "댓글 작성,조회 엔드포인트")
public class CommentController {
    private final CommentService commentService;
    private final TokenProvider tokenProvider;

    @PostMapping("/{postId}/comments")
    @Operation(summary = "댓글 작성", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = "application/json", examples =
            @ExampleObject(name = "댓글 내용", value = "{ \"content\":  }"))))
    public ResponseEntity<?> addComment(@PathVariable("postId") int postId,
                                        @RequestBody Map<String, String> requestBody,
                                        HttpServletRequest request) {
        String content = requestBody.get("content");
        String token = tokenProvider.extractTokenFromHeader(request);
        if (token == null) {
            return ResponseEntity.status(401).body("token not provided");
        }

        Integer userId = tokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401).body("not valid token.");
        }


        commentService.addComment(postId, content, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "게시글에 관한 댓글 조회")
    public ResponseEntity<?> getComments(@PathVariable("postId") int postId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size,
                                         HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromHeader(request);
        Integer userId = null;
        if (token != null) {
            userId = tokenProvider.getUserIdFromToken(token);
        }

        PageResponse<CommentResponseDto> response = commentService.getComments(postId, page, size, userId);
        return ResponseEntity.ok(response);

    }
}
