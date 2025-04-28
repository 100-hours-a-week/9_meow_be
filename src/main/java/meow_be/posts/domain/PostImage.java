package meow_be.posts.domain;

import jakarta.persistence.*;

public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private int imageNumber;
}
