package meow_be.posts.service;

import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.Post;
import meow_be.posts.dto.PostDto;
import meow_be.posts.mapper.PostMapper;
import meow_be.posts.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}