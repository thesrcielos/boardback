package com.board;

import com.board.WebSocketAuthController;
import com.board.auth.WebSocketAuthService;
import com.board.auth.user.UserDTO;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebSocketAuthController.class)
@Import({WebSocketAuthControllerIntegrationTest.MockConfig.class, WebSocketAuthControllerIntegrationTest.NoSecurityConfig.class})
class WebSocketAuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebSocketAuthService wsAuthService;
    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        public WebSocketAuthService wsAuthService() {
            return Mockito.mock(WebSocketAuthService.class);
        }
        @Bean
        @Primary
        public com.board.auth.jwt.JwtUtil jwtUtil() {
            return Mockito.mock(com.board.auth.jwt.JwtUtil.class);
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
        Mockito.reset(wsAuthService);
    }

    @Test
    void generate_returnsTokenDTO() throws Exception {
        Mockito.when(wsAuthService.generateToken(eq("1"))).thenReturn("ws-token");
        mockMvc.perform(post("/ws-auth/token")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("ws-token"));
    }

    @Test
    void validate_validToken_returnsUserDTO() throws Exception {
        UserDTO userDTO = new UserDTO("1", "Test User", "test@example.com");
        Mockito.when(wsAuthService.getUserFromToken(eq("token123"))).thenReturn(userDTO);
        mockMvc.perform(get("/ws-auth/validate/token123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void validate_invalidToken_returnsUnauthorized() throws Exception {
        Mockito.when(wsAuthService.getUserFromToken(eq("badtoken"))).thenReturn(null);
        mockMvc.perform(get("/ws-auth/validate/badtoken"))
                .andExpect(status().isUnauthorized());
    }
} 