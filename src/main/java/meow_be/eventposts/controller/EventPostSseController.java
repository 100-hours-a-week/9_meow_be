package meow_be.eventposts.controller;

import lombok.RequiredArgsConstructor;
import meow_be.eventposts.service.EventPostService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/event-posts/sse")
@RequiredArgsConstructor
public class EventPostSseController {

    private final EventPostService eventPostService;

    @GetMapping(value = "/top3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamTop3Ranking() {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                while (true) {
                    var top3 = eventPostService.getTop3Ranking();
                    emitter.send(SseEmitter.event()
                            .name("top3-update")
                            .data(top3));
                    Thread.sleep(1000);
                }
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
    @GetMapping(value = "/summary", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAllPostSummary() {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                while (true) {
                    var summary = eventPostService.getPostLikeSummary();
                    emitter.send(SseEmitter.event()
                            .name("like-update")
                            .data(summary));
                    Thread.sleep(1000);
                }
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
}
