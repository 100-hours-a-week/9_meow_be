package meow_be.posts.repository;

import meow_be.posts.domain.Post;
import meow_be.posts.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Integer> {
    List<PostImage> findByPostId(int postId);
}