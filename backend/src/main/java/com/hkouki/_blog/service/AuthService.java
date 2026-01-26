package com.hkouki._blog.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hkouki._blog.dto.LoginRequest;
import com.hkouki._blog.dto.LoginResponse;
import com.hkouki._blog.dto.RegisterRequest;
import com.hkouki._blog.entity.User;
import com.hkouki._blog.entity.Role;
import com.hkouki._blog.exception.EmailAlreadyExistsException;
import com.hkouki._blog.exception.UsernameAlreadyExistsException;
import com.hkouki._blog.exception.InvalidCredentialsException;
import com.hkouki._blog.repository.UserRepository;
import com.hkouki._blog.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
            JwtService jwtService,
            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already taken");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);

        if (userRepository.count() == 0) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        String identifier = request.getIdentifier();

        User user;

        if (identifier.contains("@")) {
            // Login with email
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        } else {
            // Login with username
            // System.out.println("=======================================");
            user = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole(), user.getUsername());
        return new LoginResponse(token);
    }
}
