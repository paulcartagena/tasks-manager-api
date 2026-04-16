package com.tasksmanager.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    private final String SECRET =
            "mysecretkeymysecretkeymysecretkeymysecretkey";

    @BeforeEach
    void SetUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
    }

    private UserDetails buildUser() {
        return User.builder()
                .username("paul@gmail.com")
                .password("123456")
                .roles("USER")
                .build();
    }

    @Test
    void shouldGenerateToken() {
        UserDetails user = buildUser();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractUsername() {
        UserDetails user = buildUser();

        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);

        assertEquals(user.getUsername(), username);
    }

    @Test
    void shouldValidateToken() {
        UserDetails user = buildUser();

        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }

    @Test
    void shouldFailInvalidToken() {
        UserDetails user = buildUser();

        String token = jwtService.generateToken(user);

        UserDetails otherUser = User.builder()
                .username("other@gmail.com")
                .password("123456")
                .roles("USER")
                .build();

        boolean isValid = jwtService.isTokenValid(token, otherUser);

        assertFalse(isValid);
    }
}
