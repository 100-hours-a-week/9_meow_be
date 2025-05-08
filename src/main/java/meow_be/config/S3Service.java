package meow_be.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final String s3Domain = "https://s3-an2-image-meowng.s3.ap-northeast-2.amazonaws.com";
    private final String cloudFrontDomain = "https://ds36vr51hmfa7.cloudfront.net";

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
                fileUrl = fileUrl.replace(s3Domain, cloudFrontDomain);  // CloudFront URL로 변환
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
}
