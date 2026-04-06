package com.tasksmanager.api.service;

import com.tasksmanager.api.dto.RegisterRequestDTO;
import com.tasksmanager.api.exception.EmailAlreadyExistsException;
import com.tasksmanager.api.model.User;
import com.tasksmanager.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for user authentication and registration.
 * Implements UserDetailsService to integrate with Spring Security.
 */
@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequestDTO dto) {
        userRepository.findByEmail(dto.getEmail())
                .ifPresent(existing -> {
                    throw new EmailAlreadyExistsException("Mail already exists.");
                });

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    /**
     * Loads a user by their email address for Spring Security authentication.
     * Called internally by Authentication Manager during login to retrieve user
     * and compare the provided password against the stored hash.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
