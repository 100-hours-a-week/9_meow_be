package meow_be.posts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostSummaryDto {
    private int id;
    private int userId;
    private String nickname;
    private String profileImageUrl;
    private String transformedContent;
    private String emotion;
    private String postType;
    private String thumbnailUrl; // thumbnailUrl로 필드명 변경
    private int commentCount;
    private int viewCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public PostSummaryDto(int id, int userId, String nickname, String profileImageUrl,
                          String transformedContent, String emotion, String postType,
                          String thumbnailUrl, int commentCount,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.transformedContent = transformedContent;
        this.emotion = emotion;
        this.postType = postType;
        this.thumbnailUrl = thumbnailUrl;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
