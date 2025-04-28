package meow_be.posts.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDto {
    private int id;
    private int userId;
    private String username;
    private String profileUrl;
    private String title;
    private String transformedContent;
    private String emotion;
    private String postType;
    private List<String> imageUrls;  // 이미지 URL 리스트
    private int likeCount;
    private int commentCount;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Public 생성자
    public PostDto(int id, int userId, String username, String profileUrl, String title, String transformedContent, String emotion, String postType, List<String> imageUrls, int likeCount, int commentCount, int viewCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.profileUrl = profileUrl;
        this.title = title;
        this.transformedContent = transformedContent;
        this.emotion = emotion;
        this.postType = postType;
        this.imageUrls = imageUrls;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
