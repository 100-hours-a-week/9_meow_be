package meow_be.users.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long kakaoId;
    private String email;
    private String nickname;
    private String animalType;
    private String profileImageUrl;
}
