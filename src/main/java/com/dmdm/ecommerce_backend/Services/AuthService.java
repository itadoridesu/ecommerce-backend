package com.dmdm.ecommerce_backend.Services;

import com.dmdm.ecommerce_backend.Config.JwtService;
import com.dmdm.ecommerce_backend.Dto.Auth.AuthRequest;
import com.dmdm.ecommerce_backend.Dto.Auth.RegisterRequest;
import com.dmdm.ecommerce_backend.Dto.Auth.AuthResponse;
import com.dmdm.ecommerce_backend.Entity.User;
import com.dmdm.ecommerce_backend.Repositories.UserRepository;
import com.dmdm.ecommerce_backend.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username/email already exists to avoid duplicates
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse("Username is already taken", null);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse("Email is already taken", null);
        }

        // Create user entity from DTO
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // secure

        // Assign default role USER
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRole(roles);

        // Save user in DB
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse("User registered successfully", token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        // 1. Find user
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return new AuthResponse("Invalid username or password", null);
        }

        // 2. Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse("Invalid username or password", null);
        }

        // 3. Generate JWT token
        String token = jwtService.generateToken(user);

        return new AuthResponse("Login successful", token);
    }

    @Transactional
    public AuthResponse registerAdmin(RegisterRequest request) {
        // Check if username/email already exists to avoid duplicates
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse("Username is already taken", null);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse("Email is already taken", null);
        }

        // Create user entity from DTO
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // secure

        // Assign default role USER
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        user.setRole(roles);

        // Save user in DB
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse("User registered successfully", token);
    }


    @Transactional(readOnly = true)
    public AuthResponse loginAdmin(AuthRequest request) {
        // 1. Find user
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return new AuthResponse("Invalid username or password", null);
        }

        // 2. Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse("Invalid username or password", null);
        }

        // 3. Generate JWT token
        String token = jwtService.generateToken(user);

        return new AuthResponse("Login successful", token);
    }
}
