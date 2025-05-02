package meow_be.users.service;

import lombok.RequiredArgsConstructor;
import meow_be.users.domain.User;
import meow_be.users.dto.UserDto;
import meow_be.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public int createUser(UserDto userDto) {
        User user = User.builder()
                .kakaoId(userDto.getKakaoId())
                .email(userDto.getEmail())
                .nickname(userDto.getNickname())
                .animalType(userDto.getAnimalType() != null ? userDto.getAnimalType() : "cat")
                .profileImageUrl(userDto.getProfileImageUrl())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }
}
