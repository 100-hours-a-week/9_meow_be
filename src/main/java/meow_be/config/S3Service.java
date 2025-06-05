package meow_be.config;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import meow_be.posts.dto.PresignedUrlRequestDto;
import meow_be.posts.dto.PresignedUrlResponseDto;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.domain}")
    private String s3Domain;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;


    // 이미지 업로드
    public List<String> uploadImages(List<MultipartFile> imageFiles) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : imageFiles) {
            try {
                String originalFilename = file.getOriginalFilename();
                String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID() + ext;

                InputStream inputStream = file.getInputStream();
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType());

                amazonS3.putObject(bucket, uniqueFileName, inputStream, metadata);

                String fileUrl = amazonS3.getUrl(bucket, uniqueFileName).toString();
                fileUrl = fileUrl.replace(s3Domain, cloudFrontDomain);
                imageUrls.add(fileUrl);
            } catch (Exception e) {
                throw new RuntimeException("S3 이미지 업로드 중 오류 발생: " + e.getMessage());
            }
        }

        return imageUrls;
    }
    public String uploadThumbnail(MultipartFile originalImage) {
        try {
            String originalFilename = originalImage.getOriginalFilename();
            String extension = getFileExtension(originalFilename).orElse("jpg").toLowerCase();
            String fileName = "thumbnails/" + UUID.randomUUID() + "." + extension;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Thumbnails.of(originalImage.getInputStream())
                    .size(150, 150)
                    .outputFormat(extension)
                    .toOutputStream(baos);
            byte[] thumbnailBytes = baos.toByteArray();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(thumbnailBytes.length);
            metadata.setContentType(originalImage.getContentType());

            ByteArrayInputStream inputStream = new ByteArrayInputStream(thumbnailBytes);
            amazonS3.putObject(bucket, fileName, inputStream, metadata);

            String fileUrl = amazonS3.getUrl(bucket, fileName).toString();
            fileUrl = fileUrl.replace(s3Domain, cloudFrontDomain);
            return fileUrl;

        } catch (IOException e) {
            throw new RuntimeException("썸네일 생성 또는 업로드 실패", e);
        }
    }

    private Optional<String> getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return Optional.empty();
        return Optional.of(filename.substring(filename.lastIndexOf('.') + 1));
    }

    public PresignedUrlResponseDto generatePresignedUrl(PresignedUrlRequestDto fileInfo, int expirationMinutes) {
        String ext = getFileExtension(fileInfo.getFileName()).orElse("jpg").toLowerCase();
        String uniqueFileName = UUID.randomUUID() + "." + ext;

        Date expiration = new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, uniqueFileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        generatePresignedUrlRequest.addRequestParameter("Content-Type", fileInfo.getFileType());

        String presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();

        return new PresignedUrlResponseDto(presignedUrl,uniqueFileName);
    }
}
