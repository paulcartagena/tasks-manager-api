package com.tasksmanager.api.controller;

import com.tasksmanager.api.dto.LoginRequestDTO;
import com.tasksmanager.api.dto.RegisterRequestDTO;
import com.tasksmanager.api.dto.TokenResponseDTO;
import com.tasksmanager.api.model.User;
import com.tasksmanager.api.service.JwtService;
import com.tasksmanager.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public TokenResponseDTO register(@Valid @RequestBody RegisterRequestDTO reqDTO) {
        User user = authService.registerUser(reqDTO);
        String token = jwtService.generateToken(user);

        return new TokenResponseDTO(token);
    }

    @PostMapping("/login")
    @Operation(summary = "Login with an existing user")
    public TokenResponseDTO login(@Valid @RequestBody LoginRequestDTO reqDTO) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(reqDTO.getEmail(), reqDTO.getPassword())
        );

        UserDetails userDetails = (User) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return new TokenResponseDTO(token);
    }
}
