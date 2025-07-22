package com.board;

import com.board.auth.controller.AuthController;
import com.board.auth.dto.LoginDTO;
import com.board.auth.dto.RegisterDTO;
import com.board.auth.dto.TokenDTO;
import com.board.auth.jwt.JwtUtil;
import com.board.auth.service.AuthService;
import com.board.auth.service.GoogleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({AuthControllerIntegrationTest.MockConfig.class, AuthControllerIntegrationTest.NoSecurityConfig.class})
class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthService authService;
    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
        @Bean
        @Primary
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }
        @Bean
        @Primary
        public GoogleService googleService(){
            return Mockito.mock(GoogleService.class);
        }
    }

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        @Primary
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(authService);
    }

    @Test
    void loginUser_returnsTokenDTO() throws Exception {
        LoginDTO loginDTO = LoginDTO.builder().email("test@example.com").password("password123").build();
        TokenDTO tokenDTO = new TokenDTO("jwt-token");
        Mockito.when(authService.login(any(LoginDTO.class))).thenReturn(tokenDTO);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void registerUser_returnsTokenDTO() throws Exception {
        RegisterDTO registerDTO = RegisterDTO.builder().name("Test User").email("test@example.com").password("password123").build();
        TokenDTO tokenDTO = new TokenDTO("jwt-token");
        Mockito.when(authService.createUser(any(RegisterDTO.class))).thenReturn(tokenDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }
} 