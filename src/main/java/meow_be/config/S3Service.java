package meow_be.config;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

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
                String s3Domain = "https://s3-an2-image-meowng.s3.ap-northeast-2.amazonaws.com";
                String cloudFrontDomain = "https://ds36vr51hmfa7.cloudfront.net/";
                fileUrl=fileUrl.replace(s3Domain, cloudFrontDomain);
                imageUrls.add(fileUrl);
            } catch (Exception e) {
                throw new RuntimeException("S3 이미지 업로드 중 오류 발생: " + e.getMessage());
            }
        }

        return imageUrls;
    }
}
