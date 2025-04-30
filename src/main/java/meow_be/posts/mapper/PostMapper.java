package meow_be.posts.mapper;

import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.Post;
import meow_be.posts.domain.PostImage;
import meow_be.posts.dto.PostDto;
import meow_be.posts.dto.PostSummaryDto;
import meow_be.posts.repository.PostImageRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final PostImageRepository postImageRepository;

    public PostDto toDto(Post post) {
        // 게시물에 관련된 이미지 URL 리스트 가져오기
        List<String> imageUrls = postImageRepository.findByPostId(post.getId()).stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());

        return new PostDto(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),  // 필드명 변경
                post.getUser().getProfileImageUrl(),  // 필드명 변경
                post.getTransformedContent(),
                post.getEmotion(),
                post.getPostType(),
                imageUrls,
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
    public PostSummaryDto toSummaryDto(Post post) {
        // number == 1인 이미지 URL → thumbnailUrl
        String thumbnailUrl = postImageRepository.findByPostId(post.getId()).stream()
                .filter(image -> image.getImageNumber() == 0)  // getImageNumber()로 수정
                .map(PostImage::getImageUrl)
                .findFirst()
                .orElse(null);


        // transformedContent 100자 제한
        String shortContent = post.getTransformedContent();
        if (shortContent != null && shortContent.length() > 100) {
            shortContent = shortContent.substring(0, 100);
        }

        return new PostSummaryDto(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getUser().getProfileImageUrl(),
                shortContent,
                post.getEmotion(),
                post.getPostType(),
                thumbnailUrl,
                post.getCommentCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }


}
