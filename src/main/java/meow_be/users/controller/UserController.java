package meow_be.users.controller;

import lombok.RequiredArgsConstructor;
import meow_be.users.dto.UserDto;
import meow_be.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Integer> createUser(
            @RequestPart("userDto") UserDto userDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        int userId = userService.createUser(userDto, profileImage);
        return ResponseEntity.ok(userId);
    }
}
