package meow_be.posts.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import meow_be.config.S3Service;
import meow_be.posts.dto.PresignedUrlRequestDto;
import meow_be.posts.dto.PresignedUrlResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    @PutMapping("/presigned-url")
    @Operation(summary = "presigned-url 생성")
    public ResponseEntity<PresignedUrlResponseDto> getPresignedUrl(
            @RequestBody PresignedUrlRequestDto requestDto) {

        PresignedUrlResponseDto url = s3Service.generatePresignedUrl(requestDto, 5);
        return ResponseEntity.ok(url);
    }
}
