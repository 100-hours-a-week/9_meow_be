package meow_be.eventposts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class EventImageRankDto {
    private Integer week;
    private String topic;
    private LocalDateTime endAt;
    private List<String> imageUrl;
}
