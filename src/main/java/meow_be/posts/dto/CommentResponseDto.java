package meow_be.posts.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDto {
    private Integer id;
    private Integer userId;
    private String nickname;
    private String profileImageUrl;
    private String transformedContent;
    private String postType;
    private String createdAt;
    private boolean isMyComment;
}

