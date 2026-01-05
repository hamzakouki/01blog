package com.hkouki._blog.service;

import com.hkouki._blog.entity.User;
import com.hkouki._blog.exception.InvalidPrincipalException;
import com.hkouki._blog.exception.ResourceNotFoundException;
import com.hkouki._blog.exception.UnauthenticatedException;
import com.hkouki._blog.repository.UserRepository;
import com.hkouki._blog.security.UserPrincipal;

import lombok.NonNull;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get the currently authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUser();
        }

        throw new InvalidPrincipalException("Invalid authentication principal type");
    }

    /**
     * Ban && unban a user
     */
    @Transactional
    public String banUser(@NonNull Long userId) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You cannot ban yourself");
        }
        if(!user.isEnabled()){
            return "User is already banned";
        }else{
            user.setEnabled(false);
            return "User banned successfully";
        }

    }

    @Transactional
    public String unbanUser(@NonNull Long userId) {
        User currentUser = getCurrentUser();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You cannot unban yourself");
        }
        if(user.isEnabled()){
            return "User is not banned";
        }else{
            user.setEnabled(true);
            return "User unbanned successfully";
        }
    }

    // Delete a user
    @Transactional()
    public void deleteUser(@NonNull Long userId) {
        User currentUser = getCurrentUser();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You cannot delete yourself");
        }
        userRepository.delete(user);
    }
}
