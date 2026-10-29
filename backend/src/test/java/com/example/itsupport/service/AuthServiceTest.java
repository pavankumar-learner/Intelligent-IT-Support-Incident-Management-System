package com.example.itsupport.service;

import com.example.itsupport.dto.LoginRequest;
import com.example.itsupport.dto.RegisterRequest;
import com.example.itsupport.dto.UserDto;
import com.example.itsupport.entity.Role;
import com.example.itsupport.entity.User;
import com.example.itsupport.exception.BadRequestException;
import com.example.itsupport.exception.ResourceNotFoundException;
import com.example.itsupport.repository.UserRepository;
import com.example.itsupport.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .name("Alex Hunter")
                .email("alex@enterprise.com")
                .password("encoded_pass")
                .role(Role.ROLE_EMPLOYEE)
                .department("Engineering")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Registering a new employee defaults role to ROLE_EMPLOYEE even if another role is passed")
    void testRegisterEnforcesEmployeeRole() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Alex Hunter");
        req.setEmail("alex@enterprise.com");
        req.setPassword("Password123!");
        req.setRole(Role.ROLE_ADMIN); // Attacker tries to self-register as ADMIN
        req.setDepartment("Engineering");

        when(userRepository.existsByEmail("alex@enterprise.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto created = authService.register(req);

        assertNotNull(created);
        assertEquals(Role.ROLE_EMPLOYEE, created.getRole(), "Public registration must strictly force ROLE_EMPLOYEE");
        assertEquals("alex@enterprise.com", created.getEmail());
    }

    @Test
    @DisplayName("Registering with duplicate email throws BadRequestException")
    void testRegisterDuplicateEmailThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("alex@enterprise.com");

        when(userRepository.existsByEmail("alex@enterprise.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("getUserById returns UserDto when found")
    void testGetUserByIdFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserDto result = authService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("alex@enterprise.com", result.getEmail());
    }

    @Test
    @DisplayName("getUserById throws ResourceNotFoundException when user does not exist")
    void testGetUserByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.getUserById(99L));
    }
}

