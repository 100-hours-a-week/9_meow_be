package meow_be.chat.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter
@Table(name = "chatrooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 10, nullable = false)
    private String title;
}
