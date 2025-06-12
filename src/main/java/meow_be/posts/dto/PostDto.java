package meow_be.posts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
@Getter@Builder
public class PostDto {
    private int id;
    private int userId;
    private String nickname;
    private String profileImageUrl;
    private String transformedContent;
    private String emotion;
    private String postType;
    private List<String> imageUrls;
    private Long commentCount;
    private Long likeCount;
    private boolean isLiked;
    private boolean isMyPost;
    private boolean isFollowing;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    public PostDto(
            int id,
            int userId,
            String nickname,
            String profileImageUrl,
            String transformedContent,
            String emotion,
            String postType,
            List<String> imageUrls,
            Long commentCount,
            Long likeCount,
            Boolean isLiked,
            Boolean isMyPost,
            Boolean isFollowing,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.transformedContent = transformedContent;
        this.emotion = emotion;
        this.postType = postType;
        this.imageUrls = imageUrls;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.isMyPost = isMyPost;
        this.isFollowing = isFollowing;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}