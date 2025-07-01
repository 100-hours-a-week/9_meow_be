package meow_be.posts.repository;

import meow_be.posts.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByIsDeletedFalse(Pageable pageable);
    Optional<Post> findByIdAndIsDeletedFalse(int id);
    Page<Post> findByIsDeletedFalseAndPostType(String postType, Pageable pageable);
    // Repository
    Page<Post> findByUserIdAndIsDeletedFalse(Integer userId, Pageable pageable);


}
