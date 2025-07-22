package com.board;

import com.board.auth.user.Role;
import com.board.auth.user.UserEntity;
import com.board.auth.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void save_and_findByEmail_success() {
        UserEntity user = new UserEntity(null, "Test User", "pass", "test@example.com", Role.USER);
        userRepository.save(user);
        Optional<UserEntity> found = userRepository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    void existsByEmail_returnsTrueIfExists() {
        UserEntity user = new UserEntity(null, "Test User", "pass", "exists@example.com", Role.USER);
        userRepository.save(user);
        assertTrue(userRepository.existsByEmail("exists@example.com"));
        assertFalse(userRepository.existsByEmail("notfound@example.com"));
    }
} 