//what we return to the client when they request user info (without exposing sensitive info like password)
package com.example.taskmanager.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String username;
    private String role;
    private String email;
}
