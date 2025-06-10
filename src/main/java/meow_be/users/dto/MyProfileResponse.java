package meow_be.users.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyProfileResponse {
    private String nickname;
    private String animalType;
    private String profileImageUrl;
    private long postCount;
    private long followerCount;
    private long followingCount;
}
