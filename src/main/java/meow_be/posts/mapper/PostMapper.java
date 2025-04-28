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
                .map(PostImage::getImageUrl)  // PostImage에서 imageUrl만 추출
                .collect(Collectors.toList());

        return new PostDto(
                post.getId(),
                post.getUser().getId(),  // 게시물 작성자의 ID
                post.getUser().getNickname(),  // 게시물 작성자의 닉네임
                post.getUser().getProfileImageUrl(),  // 게시물 작성자의 프로필 이미지 URL
                post.getTitle(),
                post.getTransformedContent(),
                post.getEmotion(),
                post.getPostType(),
                imageUrls,  // 이미지 URL 리스트
                post.getLikeCount(),
                post.getCommentCount(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
