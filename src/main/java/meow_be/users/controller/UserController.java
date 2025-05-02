package meow_be.users.controller;

import lombok.RequiredArgsConstructor;
import meow_be.users.dto.UserDto;
import meow_be.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Integer> createUser(@RequestBody UserDto userDto) {
        int userId = userService.createUser(userDto);
        return ResponseEntity.ok(userId);
    }
}
