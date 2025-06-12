package meow_be.users.service;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.config.S3Service;
import meow_be.users.domain.User;
import meow_be.users.dto.EditUserProfileRequest;
import meow_be.users.dto.EditUserProfileResponse;
import meow_be.users.dto.MyProfileResponse;
import meow_be.users.dto.UserProfileResponse;
import meow_be.users.repository.UserQueryRepository;
import meow_be.users.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

private final UserRepository userRepository;
private final S3Service s3Service;
private final UserQueryRepository userQueryRepository;

public int createUser(Long kakaoId,String nickname,String animalType, MultipartFile profileImage) {
    String profileImageUrl = null;

    if (profileImage != null && !profileImage.isEmpty()) {
        profileImageUrl= s3Service.uploadImages(List.of(profileImage)).get(0);
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
    public String getProfileImageUrlByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return user.getProfileImageUrl();
    }
    public UserProfileResponse getUserProfile(Integer targetUserId, Integer loginUserId) {
        UserProfileResponse profile = userQueryRepository.findUserProfile(targetUserId, loginUserId);
        if (profile == null) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
        return profile;
    }
    public MyProfileResponse getMyProfile(Integer userId) {
        MyProfileResponse profile = userQueryRepository.findMyProfile(userId);
        if (profile == null) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
        return profile;
    }

    public EditUserProfileResponse getEditUserProfile(Integer userId) {
        EditUserProfileResponse userInfo = userQueryRepository.findEditUserProfile(userId);
        if (userInfo == null) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
        return userInfo;
    }
    public void updateUserProfile(Integer userId, EditUserProfileRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        User updatedUser = User.builder()
                .id(existingUser.getId())
                .kakaoId(existingUser.getKakaoId())
                .nickname(request.getNickname() != null ? request.getNickname() : existingUser.getNickname())
                .animalType(request.getPostType())
                .profileImageUrl(request.getProfileImageUrl() != null ? request.getProfileImageUrl() : existingUser.getProfileImageUrl())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(updatedUser);
    }

}
