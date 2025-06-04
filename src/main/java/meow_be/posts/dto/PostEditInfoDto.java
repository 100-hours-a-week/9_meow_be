package meow_be.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostEditInfoDto {
    private int id;
    private String username;
    private String profileImageUrl;
    private String content;
}