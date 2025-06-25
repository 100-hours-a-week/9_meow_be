package meow_be.eventposts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meow_be.eventposts.domain.EventWeek;
import meow_be.eventposts.dto.EventStatusResponse;
import meow_be.eventposts.repository.EventWeekRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventWeekService {

    private final EventWeekRepository eventWeekRepository;

    public EventStatusResponse getStatus(LocalDateTime dateTime) {
        int currentWeek = getCurrentWeek();

        Optional<EventWeek> optionalWeek = eventWeekRepository.findByWeek(currentWeek);

        if (optionalWeek.isEmpty()) {
            return new EventStatusResponse(null, null, currentWeek);
        }
        log.info("현재 시간: {}", dateTime);

        EventWeek week = optionalWeek.get();
        log.info("startVoteAt: {}, endVoteAt: {}", week.getStartVoteAt(), week.getEndVoteAt());
        log.info("getstart: {}, getend: {}", week.getStartApplyAt(), week.getEndApplyAt());

        if (!dateTime.isBefore(week.getStartApplyAt()) && dateTime.isBefore(week.getEndApplyAt())) {
            // 신청 기간
            return new EventStatusResponse("신청", week.getEndApplyAt(), currentWeek);
        } else if (!dateTime.isBefore(week.getEndApplyAt()) && dateTime.isBefore(week.getStartVoteAt())) {
            // 신청 마감 ~ 투표 시작 사이
            return new EventStatusResponse("투표전", week.getStartVoteAt(), currentWeek);
        } else if (!dateTime.isBefore(week.getStartVoteAt()) && dateTime.isBefore(week.getEndVoteAt())) {
            // 투표 기간
            return new EventStatusResponse("투표중", week.getEndVoteAt(), currentWeek);
        } else {
            // 그 외 (예: 투표 끝난 후)
            return new EventStatusResponse(null, dateTime, week.getWeek());
        }
    }

    public Map<String, Object> getCurrentWeekTopic(int Currentweek) {
        Optional<EventWeek> optionalWeek = eventWeekRepository.findByWeek(Currentweek);

        if (optionalWeek.isEmpty()) {
            return Map.of(
                    "topic", null
            );
        }

        EventWeek week = optionalWeek.get();
        return Map.of(
                "topic", week.getTopic()
        );
    }
    private int getCurrentWeek() {
        LocalDate start = LocalDate.of(2025, 6, 16);
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        LocalDate now = LocalDate.now(koreaZone);
        return (int) ChronoUnit.WEEKS.between(start, now) + 1;
    }

}
