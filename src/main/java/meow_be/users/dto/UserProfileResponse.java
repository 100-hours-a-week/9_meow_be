package meow_be.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private String nickname;
    private String animalType;
    private String profileImageUrl;
    private long postCount;
    private long followerCount;
    private long followingCount;
    private boolean following;
    private boolean currentUser;
}
