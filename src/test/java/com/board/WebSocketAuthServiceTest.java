package com.board;

import com.board.auth.WebSocketAuthService;
import com.board.auth.user.Role;
import com.board.auth.user.UserDTO;
import com.board.auth.user.UserEntity;
import com.board.auth.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WebSocketAuthServiceTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    private WebSocketAuthService wsAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        wsAuthService = new WebSocketAuthService(redisTemplate, userRepository);
        // Setear el valor de ttlSeconds por reflexión
        try {
            java.lang.reflect.Field field = WebSocketAuthService.class.getDeclaredField("ttlSeconds");
            field.setAccessible(true);
            field.set(wsAuthService, 100L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void generateToken_success() {
        UserEntity user = new UserEntity("1", "Test User", "pass", "test@example.com", Role.USER);
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String token = wsAuthService.generateToken("1");
        assertNotNull(token);
        verify(redisTemplate.opsForValue()).set(eq(token), any(UserDTO.class), eq(100L), eq(java.util.concurrent.TimeUnit.SECONDS));
    }

    @Test
    void getUserFromToken_returnsUserDTO() {
        UserDTO dto = new UserDTO("1", "Test User", "test@example.com");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("token123")).thenReturn(dto);
        UserDTO result = wsAuthService.getUserFromToken("token123");
        assertEquals(dto, result);
    }

    @Test
    void isValidToken_returnsTrueOrFalse() {
        when(redisTemplate.hasKey("token123")).thenReturn(true);
        assertTrue(wsAuthService.isValidToken("token123"));
        when(redisTemplate.hasKey("token123")).thenReturn(false);
        assertFalse(wsAuthService.isValidToken("token123"));
    }

    @Test
    void invalidateToken_deletesKey() {
        wsAuthService.invalidateToken("token123");
        verify(redisTemplate).delete("token123");
    }
} 