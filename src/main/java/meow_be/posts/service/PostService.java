package meow_be.posts.service;

import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.Post;
import meow_be.posts.dto.PostDto;
import meow_be.posts.dto.PostSummaryDto;
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
}