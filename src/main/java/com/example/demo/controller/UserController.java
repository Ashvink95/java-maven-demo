package com.example.demo.controller;

import com.example.demo.dto.ForgotPasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Authentication endpoints used by the React frontend, all under /api/users:
 *   POST /signup           { fullName, email, password }
 *   POST /login            { email, password }            -> { token, fullName, email }
 *   POST /forgot-password  { email, newPassword }
 *
 * Accounts are persisted via UserRepository (H2 by default, MySQL with the
 * "mysql" profile). Login returns a signed token that protects /api/todos.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserController(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email is already registered"));
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Signup successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        String token = tokenService.generateToken(user.getId(), user.getEmail());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "fullName", user.getFullName() != null ? user.getFullName() : "",
                "email", user.getEmail()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No account found for that email"));
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password updated. Please log in."));
    }
}
