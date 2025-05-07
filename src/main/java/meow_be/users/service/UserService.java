package meow_be.users.service;

import lombok.RequiredArgsConstructor;
import meow_be.config.S3Service;
import meow_be.users.domain.User;
import meow_be.users.dto.UserDto;
import meow_be.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

private final UserRepository userRepository;
private final S3Service s3Service;

public int createUser(UserDto userDto, MultipartFile profileImage) {
    String profileImageUrl = null;

    if (profileImage != null && !profileImage.isEmpty()) {
        List<String> urls = s3Service.uploadImages(List.of(profileImage));
        profileImageUrl = urls.get(0);
    }

    User user = User.builder()
            .kakaoId(userDto.getKakaoId())
            .email(userDto.getEmail())
            .nickname(userDto.getNickname())
            .animalType(userDto.getAnimalType())
            .profileImageUrl(profileImageUrl)
            .build();

    return userRepository.save(user).getId();
}
}
