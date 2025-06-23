package meow_be.eventposts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class EventWeekRankDto {
    private Integer week;
    private String topic;
    private LocalDateTime endAt;
    private List<EventTopRankDto> rank;
}
