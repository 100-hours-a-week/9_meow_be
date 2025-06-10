package meow_be.users.repository;

import meow_be.users.dto.EditUserProfileResponse;
import meow_be.users.dto.MyProfileResponse;
import meow_be.users.dto.UserProfileResponse;

public interface UserQueryRepository {
    UserProfileResponse findUserProfile(Integer targetUserId, Integer loginUserId);
    MyProfileResponse findMyProfile(Integer userId);

    EditUserProfileResponse findEditUserProfile(Integer userId);

}
