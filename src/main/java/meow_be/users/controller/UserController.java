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
    public ResponseEntity<Integer> createUser(
            @RequestParam("kakaoId") Long kakaoId,
            @RequestParam("email") String email,
            @RequestParam("nickname") String nickname,
            @RequestParam("animalType") String animalType,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        int userId = userService.createUser(kakaoId,email,nickname, animalType,profileImage);
        return ResponseEntity.ok(userId);
    }
}
