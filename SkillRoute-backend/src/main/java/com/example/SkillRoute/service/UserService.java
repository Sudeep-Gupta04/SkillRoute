package com.example.SkillRoute.service;

import com.example.SkillRoute.model.User;
import com.example.SkillRoute.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

   // private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Registration
    public User register(String username, String email, String password) throws Exception {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(password);

        return userRepository.save(user);
    }

    // Login
    public User login(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));

        if (!password.equals(user.getPasswordHash())) {
            throw new Exception("Invalid password");
        }
        return user;
    }
}

