//DTO that servers sends back after successful login
package com.example.taskmanager.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;
    private String username;
    private String role; // contains only safe info (no DB id, password)
    private String email;

}
