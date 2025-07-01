package meow_be.eventposts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventStatusResponse {
    private String status;
    private LocalDateTime time;
    private int week;
}
