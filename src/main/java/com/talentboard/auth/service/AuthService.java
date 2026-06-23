package com.talentboard.auth.service;

import com.talentboard.auth.dto.AuthResponse;
import com.talentboard.auth.dto.LoginRequest;
import com.talentboard.common.exception.BusinessRuleException;
import com.talentboard.common.mapper.UserMapper;
import com.talentboard.security.jwt.JwtUtil;
import com.talentboard.user.dto.UserRegistrationRequest;
import com.talentboard.user.dto.UserResponse;
import com.talentboard.user.entity.User;
import com.talentboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already in use: " + request.email());
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        return userMapper.toResponse(userRepository.save(user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(request.email()).orElseThrow();
        return new AuthResponse(token, user.getEmail(),
                user.getFirstName() + " " + user.getLastName(), user.getRole());
    }
}
