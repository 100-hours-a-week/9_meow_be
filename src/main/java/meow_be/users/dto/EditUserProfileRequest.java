package meow_be.users.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditUserProfileRequest {
    private String nickname;
    private String profileImageUrl;
    private String postType;
}
