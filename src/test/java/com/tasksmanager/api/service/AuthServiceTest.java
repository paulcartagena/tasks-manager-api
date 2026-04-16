package com.tasksmanager.api.service;

import com.tasksmanager.api.dto.RegisterRequestDTO;
import com.tasksmanager.api.exception.EmailAlreadyExistsException;
import com.tasksmanager.api.model.User;
import com.tasksmanager.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setName("Paul");
        dto.setEmail("paul@gmail.com");
        dto.setPassword("123456");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Paul");
        savedUser.setEmail("paul@gmail.com");
        savedUser.setPassword("hashedPassword");

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString()))
                .thenReturn("hashedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // Act
        User result = authService.registerUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals("paul@gmail.com", result.getEmail());
    }

    @Test
    void shouldFailIfEmailExists() {
        // Arrange
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("paul@gmail.com");

        User existingUser = new User();
        existingUser.setEmail("paul@gmail.com");

        when(userRepository.findByEmail("paul@gmail.com"))
                .thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.registerUser(dto);
        });
    }

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        // Arrange
        String email = "paul@gmail.com";

        User user = new User();
        user.setEmail(email);
        user.setPassword("hashedPassword");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // Act
        var result = authService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getUsername());
    }

    @Test
    void shouldFailWhenUserNotFound() {
        // Arrange
        String email = "paul@gmail.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername(email);
        });
    }
}
