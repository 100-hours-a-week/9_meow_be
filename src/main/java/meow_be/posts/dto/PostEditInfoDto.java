package meow_be.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostEditInfoDto {
    private int id;
    private String username;
    private String profileImageUrl;
    private String content;
    private String emotion;
    private List<String> imageUrls;
}