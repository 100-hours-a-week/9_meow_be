package meow_be.users.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 보호
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더를 통한 생성만 허용
@Builder(toBuilder = true) // 기존 객체를 기반으로 새로운 객체 생성 가능하도록 설정
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    @Column(nullable = false, length = 10)
    private String nickname;

    @Column(nullable = false, length = 10)
    private String animalType = "cat";

    @Column(length = 255)
    private String profileImageUrl;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
