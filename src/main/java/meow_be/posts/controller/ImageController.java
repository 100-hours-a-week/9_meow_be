package meow_be.posts.controller;

import lombok.RequiredArgsConstructor;
import meow_be.config.S3Service;
import meow_be.posts.dto.PresignedUrlRequestDto;
import meow_be.posts.dto.PresignedUrlResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    @PutMapping("/presigned-urls")
    public ResponseEntity<Map<String, Object>> getPresignedUrls(
            @RequestBody List<PresignedUrlRequestDto> requestList) {

        List<PresignedUrlResponseDto> urls = s3Service.generatePresignedUrls(requestList, 5);

        Map<String, Object> response = new HashMap<>();
        response.put("urls", urls);
        return ResponseEntity.ok(response);
    }
}