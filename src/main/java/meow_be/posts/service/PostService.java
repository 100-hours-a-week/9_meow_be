package meow_be.posts.service;

import lombok.RequiredArgsConstructor;
import meow_be.common.exception.UnauthorizedException;
import meow_be.posts.domain.Post;
import meow_be.posts.domain.PostImage;
import meow_be.posts.dto.PostDto;
import meow_be.posts.dto.PostEditInfoDto;
import meow_be.posts.dto.PostSummaryDto;
import meow_be.posts.repository.PostImageRepository;
import meow_be.posts.repository.PostQueryRepository;
import meow_be.posts.repository.PostRepository;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final PostQueryRepository postQueryRepository;

    public Page<PostSummaryDto> getPostSummaryPage(String postType, Pageable pageable, Integer userId) {
        return postQueryRepository.findPostsByPostType(postType, pageable, userId);
    }



    @Transactional(readOnly = true)
    public PostDto getPostById(int postId,Integer userId) {
        return postQueryRepository.findPostDetailById(postId,userId);

    }

    @Transactional
    public int createPost(String content, String emotion, String postType,
                          List<String> imageUrls, String transformedContent, Integer userId) {

        String thumbnailUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;

        User user = userRepository.findById(userId)
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

    public Page<PostSummaryDto> getUserPostSummaryPage(Integer targetUserId, Integer loginUserId, Pageable pageable) {
        return postQueryRepository.findUserPostSummaryPage(targetUserId, loginUserId, pageable);
    }

    @Transactional(readOnly = true)
    public PostEditInfoDto getPostEditInfo(Integer postId, Integer userId) {
        PostEditInfoDto dto = postQueryRepository.findPostEditInfoById(postId);
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (post.getUser().getId() != userId) {
            throw new UnauthorizedException("인증되지 않은 사용자입니다.");
        }

        if (dto == null) {
            throw new IllegalArgumentException("Post not found");
        }

        return dto;
    }
    @Transactional
    public void editPost(int postId, String content, String emotion, String postType,
                         List<String> imageUrls, String transformedContent, Integer userId) {

        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (post.getUser().getId() != userId) {
            throw new UnauthorizedException("인증되지 않은 사용자입니다.");
        }

        String thumbnailUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;

        postImageRepository.deleteAllByPost(post);

        int index = 0;
        if (imageUrls != null) {
            for (String imageUrl : imageUrls) {
                PostImage postImage = PostImage.builder()
                        .post(post)
                        .imageUrl(imageUrl)
                        .imageNumber(index++)
                        .build();
                postImageRepository.save(postImage);
            }
        }

        post.update(content, emotion, postType, transformedContent, thumbnailUrl);
    }


    @Transactional
    public void deletePost(int postId, Integer userId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!Objects.equals(userId, post.getUser().getId())) {
            throw new UnauthorizedException("인증되지 않은 사용자입니다.");
        }
        post.delete();
    }

    public Page<PostSummaryDto> getFollowingPostSummaryPage(Integer userId, Pageable pageable) {
        return postQueryRepository.findFollowingPosts(userId, pageable);
    }

}