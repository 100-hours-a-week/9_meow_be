package meow_be.eventposts.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.eventposts.dto.EventStatusResponse;
import meow_be.eventposts.service.EventWeekService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@RestController
@RequestMapping("/event-weeks")
@RequiredArgsConstructor
@Slf4j
public class EventWeekController {

    private final EventWeekService eventWeekService;

    @GetMapping("/status")
    public EventStatusResponse getEventStatus() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        return eventWeekService.getStatus(now);
    }


    @GetMapping("/topic/{week}")
    public Map<String, Object> getCurrentWeekTopic(@PathVariable("week") int week) {
        return eventWeekService.getCurrentWeekTopic(week);
    }

}
