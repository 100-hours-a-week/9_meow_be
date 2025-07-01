package meow_be.posts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
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
    private String thumbnailUrl;
    private Long commentCount;
    private Long likeCount;
    private boolean isLiked;
    private boolean isMyPost;
    private boolean isFollowing;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public PostSummaryDto(
            Integer id,
            Integer userId,
            String nickname,
            String profileImageUrl,
            String transformedContent,
            String emotion,
            String postType,
            String thumbnailUrl,
            Long commentCount,
            Long likeCount,
            Boolean isLiked,
            Boolean isMyPost,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Boolean isFollowing
    ) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.transformedContent = transformedContent;
        this.emotion = emotion;
        this.postType = postType;
        this.thumbnailUrl = thumbnailUrl;
        this.commentCount=commentCount;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.isMyPost = isMyPost;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isFollowing = isFollowing;
    }

}
