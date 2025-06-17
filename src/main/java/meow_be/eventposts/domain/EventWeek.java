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

    @Column(name = "start_apply_at", nullable = false)
    private LocalDateTime startApplyAt;

    @Column(name = "end_apply_at", nullable = false)
    private LocalDateTime endApplyAt;

    @Column(name = "start_vote_at", nullable = false)
    private LocalDateTime startVoteAt;

    @Column(name = "end_vote_at", nullable = false)
    private LocalDateTime endVoteAt;

    @Column(name = "topic", nullable = false)
    private String topic;

    @OneToMany(mappedBy = "eventWeek")
    private List<EventPost> eventPosts;
}
