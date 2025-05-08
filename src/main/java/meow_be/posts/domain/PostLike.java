package meow_be.posts.domain;

import jakarta.persistence.*;
import meow_be.users.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes")
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean isLiked = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PostLike() {
    }

    public PostLike(Post post, User user, boolean isLiked) {
        this.post = post;
        this.user = user;
        this.isLiked = isLiked;
    }

    // Getter, Setter, toString 등 필요에 따라 추가
    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }


}
