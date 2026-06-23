package com.talentboard.user.service;

import com.talentboard.common.exception.ResourceNotFoundException;
import com.talentboard.common.mapper.UserMapper;
import com.talentboard.user.dto.UserResponse;
import com.talentboard.user.dto.UserUpdateRequest;
import com.talentboard.user.entity.Role;
import com.talentboard.user.entity.User;
import com.talentboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    public List<UserResponse> findByRole(Role role) {
        return userRepository.findByRole(role).stream().map(userMapper::toResponse).toList();
    }

    public UserResponse findById(Long id) {
        return userMapper.toResponse(getById(id));
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = getById(id);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void toggleEnabled(Long id) {
        User user = getById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(getById(id));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
