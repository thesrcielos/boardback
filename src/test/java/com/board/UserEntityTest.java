package com.board;

import com.board.auth.user.Role;
import com.board.auth.user.UserDTO;
import com.board.auth.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {
    @Test
    void getAuthorities_returnsRoleAuthority() {
        UserEntity user = new UserEntity("1", "Test User", "pass", "test@example.com", Role.ADMIN);
        assertEquals(List.of(new SimpleGrantedAuthority("ADMIN")), user.getAuthorities());
    }

    @Test
    void getUsername_returnsEmail() {
        UserEntity user = new UserEntity("1", "Test User", "pass", "test@example.com", Role.USER);
        assertEquals("test@example.com", user.getUsername());
    }

    @Test
    void toDTO_returnsUserDTO() {
        UserEntity user = new UserEntity("1", "Test User", "pass", "test@example.com", Role.USER);
        UserDTO dto = user.toDTO();
        assertEquals("1", dto.getId());
        assertEquals("Test User", dto.getName());
        assertEquals("test@example.com", dto.getEmail());
    }
} 