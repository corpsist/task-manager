package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserResponseDTO;
import com.example.taskmanager.dto.UserRegistrationDTO;
import com.example.taskmanager.exception.UserAlreadyExistsException;
//import com.example.taskmanager.exception.UserAlreadyExistsException;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * Register a new user
     */

    public UserResponseDTO registerUser(UserRegistrationDTO dto) {
        // Check if username already exists
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken.");
        }

        // Check if email already exists
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already taken");
        }

        // Create User Entity
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword())) // hash password
                .email(dto.getEmail())
                .role("USER") // default role
                .build();

        User savedUser = userRepository.save(user);

        // Return safe response
        return UserResponseDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Normalize role
        String role = appUser.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER"; // fallback
        } else if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return new org.springframework.security.core.userdetails.User(
                appUser.getUsername(),
                appUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role)));
    }

}
