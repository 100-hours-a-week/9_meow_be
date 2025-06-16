package meow_be.users.service;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import meow_be.config.S3Service;
import meow_be.users.domain.User;
import meow_be.users.dto.*;
import meow_be.users.repository.UserQueryRepository;
import meow_be.users.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

private final UserRepository userRepository;
private final S3Service s3Service;
private final UserQueryRepository userQueryRepository;

    public Long createUser(UserCreateRequestDto request) {
        Optional<User> existingUserOpt = userRepository.findByKakaoId(request.getKakaoId());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (existingUser.isDeleted()) {
                User restoredUser = existingUser.toBuilder()
                        .kakaoId(request.getKakaoId())
                        .nickname(request.getNickname())
                        .animalType(request.getAnimalType())
                        .profileImageUrl(request.getProfileImage())
                        .isDeleted(false)
                        .updatedAt(LocalDateTime.now())
                        .build();
                return userRepository.save(restoredUser).getKakaoId();
            } else {
                throw new IllegalArgumentException("이미 가입된 사용자입니다.");
            }
        }

        User newUser = User.builder()
                .kakaoId(request.getKakaoId())
                .nickname(request.getNickname())
                .animalType(request.getAnimalType())
                .profileImageUrl(request.getProfileImage())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(newUser).getKakaoId();
    }

    public boolean isNicknameDuplicate(String nickname, Integer userId) {
        if (userId == null) {
            return userRepository.existsByNicknameAndIsDeletedFalse(nickname);
        }

        Optional<User> userOpt = userRepository.findByIdAndIsDeletedFalse(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (nickname.equals(user.getNickname())) {
                return false;
            }
        }

        return userRepository.existsByNicknameAndIsDeletedFalse(nickname);
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
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        User deletedUser = user.toBuilder()
                .isDeleted(true)
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(deletedUser);
    }
}
