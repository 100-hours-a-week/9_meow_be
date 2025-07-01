package meow_be.posts.service;

import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.Comment;
import meow_be.posts.domain.Post;
import meow_be.posts.dto.CommentResponseDto;
import meow_be.posts.dto.PageResponse;
import meow_be.posts.repository.CommentRepository;
import meow_be.posts.repository.PostRepository;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void addComment(int postId, String content, int userId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        commentRepository.save(comment);
    }

    public PageResponse<CommentResponseDto> getComments(int postId, int page, int size, Integer userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findByPostIdAndIsDeletedFalse(postId, pageable);

        List<CommentResponseDto> content = commentPage.getContent().stream()
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .userId(comment.getUser().getId())
                        .nickname(comment.getUser().getNickname())
                        .profileImageUrl(comment.getUser().getProfileImageUrl())
                        .transformedContent(comment.getContent())
                        .postType(comment.getUser().getAnimalType())
                        .createdAt(comment.getCreatedAt().toString())
                        .isMyComment(userId != null && comment.getUser().getId()==(userId))
                        .build())
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                commentPage.getNumber(),
                commentPage.getTotalPages(),
                commentPage.getTotalElements(),
                commentPage.getSize(),
                commentPage.isLast()
        );
    }

}
