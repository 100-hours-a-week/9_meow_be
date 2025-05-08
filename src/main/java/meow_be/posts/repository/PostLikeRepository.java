package meow_be.posts.repository;

import meow_be.posts.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    Optional<PostLike> findByPostIdAndUserId(int postId, int userId);
}