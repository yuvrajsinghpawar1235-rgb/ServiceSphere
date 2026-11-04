package com.servicesphere.service;

import com.servicesphere.entity.User;
import com.servicesphere.exception.ResourceNotFoundException;
import com.servicesphere.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User createUser(User user) {
        // In production, password hashing (BCrypt) should happen here
        return userRepository.save(user);
    }

    public User updateStatus(Long id, String status) {
        User user = getUserById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }
}