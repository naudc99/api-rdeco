package es.ndc.api_rdeco.controllers;
import java.util.List;

import es.ndc.api_rdeco.entities.UserEntity;
import es.ndc.api_rdeco.models.PasswordUpdateDto;
import es.ndc.api_rdeco.models.ResponseDto;
import es.ndc.api_rdeco.models.UserDto;
import es.ndc.api_rdeco.models.UserRolesDto;
import es.ndc.api_rdeco.services.AuthService;
import es.ndc.api_rdeco.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<List<UserRolesDto>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<UserRolesDto> getUserRoles(@PathVariable Long userId) {
        return userService.getUserRoles(userId);
    }

    @PatchMapping("/{userId}/name")
    public ResponseEntity<UserDto> updateName(@PathVariable Long userId, @RequestBody String nameNew) {
        return userService.updateName(userId, nameNew);
    }

    @PatchMapping("/{userId}/email")
    public ResponseEntity<UserDto> updateEmail(@PathVariable Long userId, @RequestBody String emailNew) {
        return userService.updateEmail(userId, emailNew);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<UserDto> updatePassword(@PathVariable Long userId,
                                                  @RequestBody PasswordUpdateDto passwords) {
        return userService.updatePassword(userId, passwords);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDto> removeUser(@PathVariable Long userId) {
        return userService.removeUser(userId);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseDto> updateUser(@PathVariable Long userId, @RequestBody UserEntity updatedUser) {
        return userService.updateUser(userId, updatedUser);
    }
}