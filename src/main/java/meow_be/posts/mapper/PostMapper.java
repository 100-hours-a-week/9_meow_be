package meow_be.posts.mapper;

import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.Post;
import meow_be.posts.domain.PostImage;
import meow_be.posts.dto.PostDto;
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
                post.getTitle(),
                post.getTransformedContent(),
                post.getEmotion(),
                post.getPostType(),
                imageUrls,
                post.getLikeCount(),
                post.getCommentCount(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

}
