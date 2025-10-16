package com.taskmanager.config;

import com.taskmanager.model.User;
import com.taskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create demo user if it doesn't exist
        if (userRepository.findByUsername("demo").isEmpty()) {
            User demoUser = new User();
            demoUser.setUsername("demo");
            demoUser.setPassword(passwordEncoder.encode("password"));
            demoUser.setEmail("demo@example.com");
            demoUser.setEnabled(true);
            userRepository.save(demoUser);
            System.out.println("✅ Created demo user (username: demo, password: password)");
        } else {
            System.out.println("✅ Demo user already exists");
        }
    }
}
