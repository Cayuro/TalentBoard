package com.talentboard.config;

import com.talentboard.user.entity.Role;
import com.talentboard.user.entity.User;
import com.talentboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createUserIfAbsent("Admin", "TalentBoard", "admin@talentboard.com", "admin123", Role.ADMIN);
        createUserIfAbsent("Recruiter", "TalentBoard", "recruiter@talentboard.com", "recruiter123", Role.RECRUITER);
        createUserIfAbsent("Candidate", "TalentBoard", "candidate@talentboard.com", "candidate123", Role.CANDIDATE);
    }

    private void createUserIfAbsent(String firstName, String lastName, String email, String password, Role role) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .enabled(true)
                    .build());
        }
    }
}
