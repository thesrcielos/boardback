package com.board.auth;

import com.board.auth.user.UserDTO;
import com.board.auth.user.UserEntity;
import com.board.auth.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service class thar offers utils for WS tokens
 */
@Service
public class WebSocketAuthService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    @Value("${ws.token.ttl-seconds:10000}")
    private long ttlSeconds;

    public WebSocketAuthService(RedisTemplate<String, Object> redisTemplate, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    public String generateToken(String id) {
        String token = UUID.randomUUID().toString();
        System.out.println("id = " + id);
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User Not found"));
        redisTemplate.opsForValue().set(token, user.toDTO(), ttlSeconds, TimeUnit.SECONDS);
        return token;
    }

    public UserDTO getUserFromToken(String token) {
        return (UserDTO)redisTemplate.opsForValue().get(token);
    }

    public boolean isValidToken(String token) {
        return redisTemplate.hasKey(token);
    }

    public void invalidateToken(String token) {
        redisTemplate.delete(token);
    }
}
