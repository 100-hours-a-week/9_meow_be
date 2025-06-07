package meow_be.posts.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostEditRequestDto {
    private String content;
    private String emotion;
    private List<String> imageUrls;
}
