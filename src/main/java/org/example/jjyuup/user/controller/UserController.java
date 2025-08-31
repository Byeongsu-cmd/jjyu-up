package org.example.jjyuup.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jjyuup.user.dto.UserRequestDto;
import org.example.jjyuup.user.dto.UserResponseDto;
import org.example.jjyuup.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll(
    ) {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getById(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        return ResponseEntity.ok(userService.update(userId, userRequestDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long userId
    ) {
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/restoreUsers")
    public ResponseEntity<Void> restore(
            @PathVariable Long userId
    ){
        userService.restore(userId);
        return ResponseEntity.ok().build();
    }
}
