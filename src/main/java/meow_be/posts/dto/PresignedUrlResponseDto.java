package meow_be.posts.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlResponseDto {
    private String presignedUrl;
}
