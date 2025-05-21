package meow_be.posts.repository;
import meow_be.posts.dto.PostSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface PostQueryRepository {
    Page<PostSummaryDto> findPostsByPostType(String postType, Pageable pageable, Integer userId);
    Page<PostSummaryDto> findUserPostSummaryPage(Integer userId, Pageable pageable);

}
