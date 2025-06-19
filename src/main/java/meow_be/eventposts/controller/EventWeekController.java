package meow_be.eventposts.controller;

import lombok.RequiredArgsConstructor;
import meow_be.eventposts.dto.EventStatusResponse;
import meow_be.eventposts.service.EventWeekService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/event-weeks")
@RequiredArgsConstructor
public class EventWeekController {

    private final EventWeekService eventWeekService;

    @GetMapping("/status")
    public EventStatusResponse getEventStatus(
            @RequestParam("datetime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime datetime
    ) {
        return eventWeekService.getStatus(datetime);
    }

    @GetMapping("/topic")
    public Map<String, Object> getCurrentWeekTopic() {
        return eventWeekService.getCurrentWeekTopic();
    }

}
