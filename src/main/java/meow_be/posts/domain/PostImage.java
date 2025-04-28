package meow_be.posts.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 기본 생성자 보호
@AllArgsConstructor
@Builder(toBuilder = true)
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private int imageNumber;
}
