package meow_be.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowUserDto {
    private Integer userId;
    private String nickname;
    private String postType;
    private String profileImageUrl;
}
