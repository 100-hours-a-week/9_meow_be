package meow_be.eventposts.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "event_weeks")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventWeek {

    @Id
    @Column(name = "week")
    private int week;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "apply_start_at", nullable = false)
    private LocalDateTime startApplyAt;

    @Column(name = "apply_end_at", nullable = false)
    private LocalDateTime endApplyAt;

    @Column(name = "vote_start_at", nullable = false)
    private LocalDateTime startVoteAt;

    @Column(name = "vote_end_at", nullable = false)
    private LocalDateTime endVoteAt;

    @OneToMany(mappedBy = "eventWeek")
    private List<EventPost> eventPosts;
}
