package com.ecommerce.backend_orbyte.config;

import com.ecommerce.backend_orbyte.entity.User;
import com.ecommerce.backend_orbyte.entity.UserRole;
import com.ecommerce.backend_orbyte.entity.UserStatus;
import com.ecommerce.backend_orbyte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:admin@orbyte.ai}")
    private String adminEmail;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            userRepository.save(User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .fullName("Admin Orbyte")
                    .role(UserRole.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .department("IT")
                    .active(true)
                    .build());
            log.info("Admin user created: {}", adminEmail);
        } else {
            log.info("Admin user already exists: {}", adminEmail);
        }
    }
}