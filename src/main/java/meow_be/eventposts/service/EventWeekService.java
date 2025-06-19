package meow_be.eventposts.service;

import lombok.RequiredArgsConstructor;
import meow_be.eventposts.domain.EventWeek;
import meow_be.eventposts.dto.EventStatusResponse;
import meow_be.eventposts.repository.EventWeekRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventWeekService {

    private final EventWeekRepository eventWeekRepository;

    public EventStatusResponse getStatus(LocalDateTime dateTime) {
        Optional<EventWeek> optionalWeek = eventWeekRepository.findByDate(dateTime);

        if (optionalWeek.isEmpty()) {
            return new EventStatusResponse(null, null);
        }

        EventWeek week = optionalWeek.get();

        if (!dateTime.isBefore(week.getStartApplyAt()) && dateTime.isBefore(week.getEndApplyAt())) {
            // 신청 기간
            return new EventStatusResponse("신청", week.getEndApplyAt());
        } else if (!dateTime.isBefore(week.getEndApplyAt()) && dateTime.isBefore(week.getStartVoteAt())) {
            // 신청 마감 ~ 투표 시작 사이
            return new EventStatusResponse("투표전", week.getStartVoteAt());
        } else if (!dateTime.isBefore(week.getStartVoteAt()) && dateTime.isBefore(week.getEndVoteAt())) {
            // 투표 기간
            return new EventStatusResponse("투표중", week.getEndVoteAt());
        } else {
            // 그 외 (예: 투표 끝난 후)
            return new EventStatusResponse(null, null);
        }
    }
    public Map<String, Object> getCurrentWeekTopic() {
        LocalDateTime now = LocalDateTime.now();
        Optional<EventWeek> optionalWeek = eventWeekRepository.findByDate(now);

        if (optionalWeek.isEmpty()) {
            return Map.of(
                    "week", null,
                    "topic", null
            );
        }

        EventWeek week = optionalWeek.get();
        return Map.of(
                "week", week.getWeek(),
                "topic", week.getTopic()
        );
    }

}
