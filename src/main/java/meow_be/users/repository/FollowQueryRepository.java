package meow_be.users.repository;

import meow_be.users.dto.FollowUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowQueryRepository {
    Page<FollowUserDto> findFollowingsByUserId(Integer userId, Pageable pageable);
}
