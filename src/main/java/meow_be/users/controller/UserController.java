package meow_be.users.controller;

import lombok.RequiredArgsConstructor;
import meow_be.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Long> createUser(
            @RequestParam("kakaoId") Long kakaoId,
            @RequestParam("nickname") String nickname,
            @RequestParam("animalType") String animalType,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        int userId = userService.createUser(kakaoId,nickname, animalType,profileImage);
        return ResponseEntity.ok(kakaoId);
    }
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
        boolean isDuplicate = userService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(isDuplicate);
    }
}
