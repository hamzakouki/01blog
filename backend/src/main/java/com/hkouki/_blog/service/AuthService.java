package com.hkouki._blog.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.hkouki._blog.entity.User;
import com.hkouki._blog.dto.LoginRequest;
import com.hkouki._blog.dto.LoginResponse;
import com.hkouki._blog.dto.RegisterRequest;
import com.hkouki._blog.repository.UserRepository;
// import com.hkouki._blog.service.JwtService;
import com.hkouki._blog.security.JwtService;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // this is logic for registration
    public void register(RegisterRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        User user = new User();

        long howMuchUsersindb = userRepository.count();
        if (howMuchUsersindb == 0) {
            user.setRole(com.hkouki._blog.entity.Role.ADMIN); // First user is ADMIN
        } else {
            user.setRole(com.hkouki._blog.entity.Role.USER); // Others are USER
        }
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        // 1 — Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // 2 — Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 3 — Generate JWT using username
        String token = jwtService.generateToken(user.getUsername());

        // 4 — Return token
        return new LoginResponse(token);
    }

}
