package com.board.auth.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("sesion")
public class UserSession {
    @Id
    private String id;
    private String email;
}