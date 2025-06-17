package meow_be.eventposts.domain;

import jakarta.persistence.*;
import lombok.*;
import meow_be.users.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EventPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week", nullable = false)
    private EventWeek eventWeek;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "ranking")
    private int ranking;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
