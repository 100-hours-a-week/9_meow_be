package meow_be.posts.service;

import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.Post;
import meow_be.posts.dto.PostDto;
import meow_be.posts.mapper.PostMapper;
import meow_be.posts.repository.PostRepository;
import meow_be.users.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;  // PostMapper 주입

    public Page<PostDto> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findByIsDeletedFalse(pageable);  // 삭제되지 않은 게시물만 조회
        return posts.map(postMapper::toDto);  // Post -> PostDto로 변환 (PostMapper 사용)
    }

    @Transactional
    public void createDummyPost() {
        // 더미 User 하나 가져오기 (id=1이라고 가정);

        User dummyUser = User.builder()
                .id(1) // id만 채운 User 객체
                .build();

        Post post = Post.builder()
                .user(dummyUser)
                .title("더미 제목")
                .content("더미 내용")
                .emotion("기쁨")
                .postType("고양이")
                .transformedContent("변환된 더미 내용")
                .isDeleted(Boolean.FALSE)
                .likeCount(0)
                .commentCount(0)
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
    }
}