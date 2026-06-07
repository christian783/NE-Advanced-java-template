package io.app.my_app.config;

import io.app.my_app.model.User;
import io.app.my_app.model.enums.Role;
import io.app.my_app.model.enums.UserStatus;
import io.app.my_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Initializes an admin user on application startup when one does not already exist.
 *
 * Configuration-driven values (email/password/fullName/phone) are read from properties:
 * - application.admin.email (default: admin@myapp.local)
 * - application.admin.password (default: admin)
 * - application.admin.full-name (default: System Administrator)
 * - application.admin.phone-number (default: 0000000000)
 *
 * NOTE: Avoid storing real secrets in application.properties for production. Prefer external secret management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.admin.email:admin@myapp.local}")
    private String adminEmail;

    @Value("${application.admin.password:admin}")
    private String adminPassword;

    @Value("${application.admin.full-name:System Administrator}")
    private String adminFullName;

    @Value("${application.admin.phone-number:+25078000000}")
    private String adminPhoneNumber;

    @Override
    public void run(String... args) throws Exception {
        try {
            Optional<User> existing = userRepository.findByEmail(adminEmail);
            if (existing.isPresent()) {
                log.info("Admin user already exists with email {}", adminEmail);
                return;
            }

            User admin = User.builder()
                    .fullName(adminFullName)
                    .email(adminEmail)
                    .phoneNumber(adminPhoneNumber)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();

            userRepository.save(admin);
            log.info("Created initial admin user with email {}", adminEmail);
        } catch (Exception ex) {
            log.error("Failed to ensure admin user exists: {}", ex.getMessage(), ex);
        }
    }
}

