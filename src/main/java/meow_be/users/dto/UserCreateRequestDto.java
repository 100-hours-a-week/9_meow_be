package meow_be.users.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserCreateRequestDto {
    private Long kakaoId;
    private String nickname;
    private String animalType;
    private String profileImage;
}
