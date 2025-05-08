package meow_be.posts.mapper;

import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.Post;
import meow_be.posts.domain.PostImage;
import meow_be.posts.dto.PostDto;
import meow_be.posts.dto.PostSummaryDto;
import meow_be.posts.repository.PostImageRepository;
import meow_be.posts.repository.PostLikeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;

    public PostDto toDto(Post post) {
        List<String> imageUrls = postImageRepository.findByPostId(post.getId()).stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());

        return new PostDto(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getUser().getProfileImageUrl(),
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
    public PostSummaryDto toSummaryDto(Post post, Integer userId) {
        String thumbnailUrl = postImageRepository.findByPostId(post.getId()).stream()
                .filter(image -> image.getImageNumber() == 0)
                .map(PostImage::getImageUrl)
                .findFirst()
                .orElse(null);

        boolean isLiked = false;
        if (userId != null) {
            isLiked = postLikeRepository.existsByPostIdAndUserIdAndIsLikedTrue(post.getId(), userId);
        }
        int likeCount = postLikeRepository.countByPostIdAndIsLikedTrue(post.getId());
        return new PostSummaryDto(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getUser().getProfileImageUrl(),
                post.getTransformedContent(),
                post.getEmotion(),
                post.getPostType(),
                thumbnailUrl,
                post.getCommentCount(),
                likeCount,
                isLiked,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    // 기존 버전 (userId 없는 경우)
    public PostSummaryDto toSummaryDto(Post post) {
        return toSummaryDto(post, null);
    }


}
