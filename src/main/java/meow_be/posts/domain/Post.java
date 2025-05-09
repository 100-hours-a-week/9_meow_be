package meow_be.posts.domain;

import jakarta.persistence.*;
import lombok.*;
import meow_be.users.domain.User;

import java.time.LocalDateTime;
@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 기본 생성자 보호
@AllArgsConstructor
@Builder(toBuilder = true)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String content;

    @Enumerated(EnumType.STRING)
    private Emotion emotion;

    @Column(nullable = false, length = 10)
    private String postType;

    @Column(nullable = false, length = 400)
    private String transformedContent;

    @Column(length = 500)
    private String thumbnailUrl;


    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Integer commentCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
