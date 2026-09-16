package com.example.itsupport.controller;

import com.example.itsupport.dto.JwtResponse;
import com.example.itsupport.dto.LoginRequest;
import com.example.itsupport.dto.RegisterRequest;
import com.example.itsupport.dto.UserDto;
import com.example.itsupport.entity.User;
import com.example.itsupport.repository.UserRepository;
import com.example.itsupport.security.UserPrincipal;
import com.example.itsupport.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findById(currentUser.getId()).orElse(null);
        return ResponseEntity.ok(UserDto.fromEntity(user));
    }
}
