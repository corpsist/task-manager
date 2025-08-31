package com.example.taskmanager.service;

import java.util.HashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.example.taskmanager.dto.AuthRequestDTO;
import com.example.taskmanager.dto.AuthResponseDTO;
//import com.example.taskmanager.dto.UserRegistrationDTO;
//import com.example.taskmanager.dto.UserResponseDTO;
//import com.example.taskmanager.exception.InvalidCredentialsException;
//import com.example.taskmanager.exception.UserAlreadyExistsException;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthResponseDTO login(AuthRequestDTO request) {
        try {
            // 1. Authenticate username + password
            // Authentication authentication =
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            // 2. Get the authenticated user from DB
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 3. Generate JWT token
            String token = jwtService.generateToken(new HashMap<>(), user.getUsername());

            // 4. Return full response
            return AuthResponseDTO.builder()
                    .token(token)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    /*
     * Logout (optional, only meaningful if you maintain a token blacklist)
     */

    public void logout(String token) {
        // For stateless JWT authentication, logout is typically handled client-side
        // If we want server-side handling of logout, store token in a blacklist/cache
    }
}