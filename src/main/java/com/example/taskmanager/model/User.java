package com.example.taskmanager.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // will be encrypted

    @Column(nullable = false)
    private String role; // eg: "ROLE_USER", "ROLE_ADMIN"

    @Column(nullable = false, unique = true)
    private String email;
}
