package meow_be.eventposts.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.eventposts.dto.EventPostRankingDto;
import meow_be.eventposts.dto.EventPostRequest;
import meow_be.eventposts.service.EventPostService;
import meow_be.login.security.TokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/event-posts")
@RequiredArgsConstructor
@Tag(name="이벤트 게시글 컨트롤러",description = "이벤트 게시글 작성,랭킹 조회")
public class EventPostController {
    private final EventPostService eventPostService;
    private final TokenProvider tokenProvider;

    @PostMapping("/{eventPostId}/likes")
    public ResponseEntity<?> likeEventPost(@PathVariable Integer eventPostId) {
        eventPostService.likeEventPost(eventPostId);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<?> createEventPost(@RequestBody EventPostRequest request,
                                             HttpServletRequest httpRequest) {
        String token = tokenProvider.extractTokenFromHeader(httpRequest);
        Integer userId = tokenProvider.getUserIdFromToken(token);
        Integer postId = eventPostService.createEventPost(userId, request.getImageUrl());

        return ResponseEntity.ok(Map.of(
                "postId", postId,
                "message", "이벤트 게시물이 성공적으로 등록되었습니다."
        ));
    }
    @GetMapping("/applied")
    public ResponseEntity<?> checkUserApplied(HttpServletRequest httpRequest) {
        String token = tokenProvider.extractTokenFromHeader(httpRequest);
        Integer userId = tokenProvider.getUserIdFromToken(token);

        boolean hasApplied = eventPostService.hasAppliedToCurrentWeek(userId);
        return ResponseEntity.ok(Map.of("hasApplied", hasApplied));
    }
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllEventPosts() {
        return ResponseEntity.ok(eventPostService.getAllCachedEventPosts());
    }
    @GetMapping("/{rankWeek}")
    public ResponseEntity<List<EventPostRankingDto>> getRankedPostsByWeek(
            @PathVariable("rankWeek") int rankWeek) {
        List<EventPostRankingDto> rankedPosts = eventPostService.getRankedPostsByWeek(rankWeek);
        return ResponseEntity.ok(rankedPosts);
    }
    @GetMapping("/test")
    public ResponseEntity<String> testSaveWeeklyRanking(@RequestParam("week") int week) {
        eventPostService.saveWeeklyRanking(week);
        return ResponseEntity.ok("saveWeeklyRanking executed for week " + week);
    }


}
