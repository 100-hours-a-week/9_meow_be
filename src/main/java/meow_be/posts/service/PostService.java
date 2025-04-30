package meow_be.posts.service;

import lombok.RequiredArgsConstructor;
import meow_be.config.S3Service;
import meow_be.posts.domain.Post;
import meow_be.posts.domain.PostImage;
import meow_be.posts.dto.PostDto;
import meow_be.posts.dto.PostSummaryDto;
import meow_be.posts.mapper.PostMapper;
import meow_be.posts.repository.PostImageRepository;
import meow_be.posts.repository.PostRepository;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;  // PostMapper 주입
    private final S3Service s3Service;
    private final PostImageRepository postImageRepository;

    public List<PostSummaryDto> getPostSummaries(Pageable pageable) {
        return postRepository.findByIsDeletedFalse(pageable)
                .stream()
                .map(postMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostDto getPostById(int postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
        return postMapper.toDto(post);

    }

    @Transactional
    public void createPost(String content, String emotion, String postType,
                           List<MultipartFile> images) {

        List<String> imageUrls = s3Service.uploadImages(images);
        User user = userRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("User not found")); // 지금은 강제 입력이지만 추후 수정해야함

        Post post = Post.builder()
                .user(user)
                .content(content)
                .emotion(emotion)
                .postType(postType)
                .transformedContent(content)
                .likeCount(0)
                .commentCount(0)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        postRepository.save(post);

        // 3. 이미지 URL 저장
        int index = 0;
        for (String imageUrl : imageUrls) {
            PostImage postImage = PostImage.builder()
                    .post(post)
                    .imageUrl(imageUrl)
                    .imageNumber(index++) // 순서 보장
                    .build();
            postImageRepository.save(postImage);
        }
    }

}