package meow_be.users.service;

import lombok.RequiredArgsConstructor;
import meow_be.config.S3Service;
import meow_be.users.domain.User;
import meow_be.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

private final UserRepository userRepository;
private final S3Service s3Service;

public int createUser(Long kakaoId,String nickname,String animalType, MultipartFile profileImage) {
    String profileImageUrl = null;

    if (profileImage != null && !profileImage.isEmpty()) {
        profileImageUrl= s3Service.uploadImages(List.of(profileImage)).toString();
    }
    User user = User.builder()
            .kakaoId(kakaoId)
            .nickname(nickname)
            .animalType(animalType)
            .profileImageUrl(profileImageUrl)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    return userRepository.save(user).getId();
}
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
