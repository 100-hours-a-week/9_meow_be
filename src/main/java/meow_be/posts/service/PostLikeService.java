package meow_be.posts.service;

import lombok.RequiredArgsConstructor;
import meow_be.posts.domain.Post;
import meow_be.posts.domain.PostLike;
import meow_be.posts.repository.PostRepository;
import meow_be.posts.repository.PostLikeRepository;
import meow_be.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


        // 좋아요 상태 변경
        @Transactional
        public void toggleLike(int postId, int userId, boolean isLiked) {
            Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않거나 삭제된 게시글입니다."));


            PostLike existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId).orElse(null);

            if (isLiked) {
                if (existingLike != null) {
                    existingLike.setIsLiked(false);
                    postLikeRepository.save(existingLike);
                } else {
                    PostLike newLike = new PostLike(post, userRepository.getReferenceById(userId), true);
                    postLikeRepository.save(newLike);
                }
            } else {
                if (existingLike != null) {
                    existingLike.setIsLiked(true);
                    postLikeRepository.save(existingLike);
                } else {
                    PostLike newLike = new PostLike(post, userRepository.getReferenceById(userId), true);
                    postLikeRepository.save(newLike);
                }
            }
        }
}