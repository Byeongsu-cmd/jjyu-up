package org.example.jjyuup.user.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String name;
    private final String email;
    private final LocalDateTime createAt;
    private final LocalDateTime modifiedAt;

    public UserResponseDto( Long id, String name, String email, LocalDateTime createAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
    }
}
