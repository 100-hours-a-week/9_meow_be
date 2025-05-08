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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final S3Service s3Service;
    private final PostImageRepository postImageRepository;

    public Page<PostSummaryDto> getPostSummaryPage(Pageable pageable) {
        return postRepository.findByIsDeletedFalse(pageable)
                .map(postMapper::toSummaryDto);
    }


    @Transactional(readOnly = true)
    public PostDto getPostById(int postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
        return postMapper.toDto(post);

    }

    @Transactional
    public int createPost(String content, String emotion, String postType,
                          List<MultipartFile> images, String transformedContent) {

        List<MultipartFile> filteredImages = (images != null && !images.isEmpty())
                ? images.stream()
                .filter(file -> !file.isEmpty() && file.getOriginalFilename() != null)
                .collect(Collectors.toList())
                : List.of();

        List<String> imageUrls = s3Service.uploadImages(filteredImages);

        String thumbnailUrl = null;
        if (!filteredImages.isEmpty()) {
            MultipartFile firstImage = filteredImages.get(0);
            thumbnailUrl = s3Service.uploadThumbnail(firstImage);
        }

        User user = userRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = Post.builder()
                .user(user)
                .content(content)
                .emotion(emotion)
                .postType(postType)
                .transformedContent(transformedContent)
                .thumbnailUrl(thumbnailUrl)
                .likeCount(0)
                .commentCount(0)
                .isDeleted(false)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .updatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
        postRepository.save(post);

        int index = 0;
        for (String imageUrl : imageUrls) {
            PostImage postImage = PostImage.builder()
                    .post(post)
                    .imageUrl(imageUrl)
                    .imageNumber(index++)
                    .build();
            postImageRepository.save(postImage);
        }

        return post.getId();
    }



}