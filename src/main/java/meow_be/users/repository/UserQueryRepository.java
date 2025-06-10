package meow_be.users.repository;

import meow_be.users.dto.UserProfileResponse;

public interface UserQueryRepository {
    UserProfileResponse findUserProfile(Integer targetUserId, Integer loginUserId);
}
