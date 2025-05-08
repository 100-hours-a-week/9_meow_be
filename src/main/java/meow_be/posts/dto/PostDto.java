package meow_be.posts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
public class PostDto {
    private int id;
    private int userId;
    private String nickname;  // 변경된 필드명
    private String profileImageUrl;  // 변경된 필드명
    private String transformedContent;
    private String emotion;
    private String postType;
    private List<String> imageUrls;
    private int commentCount;
    private int likeCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 포맷 지정
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 포맷 지정
    private LocalDateTime updatedAt;

    // Public 생성자
    public PostDto(int id, int userId, String nickname, String profileImageUrl, String transformedContent, String emotion, String postType, List<String> imageUrls, int likeCount, int commentCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.transformedContent = transformedContent;
        this.emotion = emotion;
        this.postType = postType;
        this.imageUrls = imageUrls;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}