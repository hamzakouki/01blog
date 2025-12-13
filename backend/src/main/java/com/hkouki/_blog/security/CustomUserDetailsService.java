package com.hkouki._blog.security;

import com.hkouki._blog.entity.User;
import com.hkouki._blog.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Find user by username or email
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));

        // Convert your User entity to Spring Security UserDetails
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())               // username
                .password(user.getPassword())                  // hashed password
                .roles(user.getRole().name())                  // role from enum
                .build();
    }
}
