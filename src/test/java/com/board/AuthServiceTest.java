package com.board;

import com.board.auth.dto.LoginDTO;
import com.board.auth.dto.RegisterDTO;
import com.board.auth.dto.TokenDTO;
import com.board.auth.jwt.JwtUtil;
import com.board.auth.service.AuthService;
import com.board.auth.user.Role;
import com.board.auth.user.UserEntity;
import com.board.auth.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(jwtUtil, userRepository, authenticationManager, passwordEncoder, jwtUtil);
    }

    @Test
    void createUser_success() {
        RegisterDTO registerDTO = RegisterDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();
        when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPassword");
        UserEntity savedUser = new UserEntity(null, "Test User", "encodedPassword", "test@example.com", Role.USER);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(jwtUtil.getToken(savedUser.getEmail())).thenReturn("jwt-token");

        TokenDTO tokenDTO = authService.createUser(registerDTO);
        assertNotNull(tokenDTO);
        assertEquals("jwt-token", tokenDTO.getToken());
    }

    @Test
    void createUser_emailInUse_throwsException() {
        RegisterDTO registerDTO = RegisterDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();
        when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(true);
        assertThrows(RuntimeException.class, () -> authService.createUser(registerDTO));
    }

    @Test
    void login_success() {
        LoginDTO loginDTO = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();
        UserEntity user = new UserEntity("1", "Test User", "encodedPassword", "test@example.com", Role.USER);
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.getToken(user.getEmail())).thenReturn("jwt-token");

        TokenDTO tokenDTO = authService.login(loginDTO);
        assertNotNull(tokenDTO);
        assertEquals("jwt-token", tokenDTO.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_userNotFound_throwsException() {
        LoginDTO loginDTO = LoginDTO.builder()
                .email("notfound@example.com")
                .password("password123")
                .build();
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> authService.login(loginDTO));
    }
} 