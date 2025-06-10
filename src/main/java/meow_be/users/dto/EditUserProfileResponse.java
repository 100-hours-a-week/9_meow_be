package meow_be.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class EditUserProfileResponse {
    private String nickname;
    private String profileImageUrl;
}
