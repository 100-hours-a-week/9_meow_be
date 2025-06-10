package meow_be.users.repository;

import meow_be.users.dto.EditUserProfileResponse;
import meow_be.users.dto.UserProfileResponse;

public interface UserQueryRepository {
    UserProfileResponse findUserProfile(Integer targetUserId, Integer loginUserId);
    EditUserProfileResponse findEditUserProfile(Integer userId);

}
