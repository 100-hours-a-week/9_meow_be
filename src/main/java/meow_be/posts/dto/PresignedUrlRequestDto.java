package meow_be.posts.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PresignedUrlRequestDto {
    private String filename;
    private String fileType;
}