package com.board.auth.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Getter
@Setter
@RedisHash("WsToken")
public class UserDTO {
    private String id;
    private String name;
    private String email;
}
