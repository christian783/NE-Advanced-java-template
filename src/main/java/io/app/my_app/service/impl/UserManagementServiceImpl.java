package io.app.my_app.service.impl;

import io.app.my_app.exception.DuplicateRecordException;
import io.app.my_app.exception.EntityNotFoundException;
import io.app.my_app.mapper.UserMapper;
import io.app.my_app.model.User;
import io.app.my_app.model.dtos.user.UserCreateRequest;
import io.app.my_app.model.dtos.user.UserFilter;
import io.app.my_app.model.dtos.user.UserResponse;
import io.app.my_app.model.enums.UserStatus;
import io.app.my_app.repository.UserRepository;
import io.app.my_app.service.UserManagementService;
import io.app.my_app.specification.UserSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserSpec userSpec;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(UserFilter filter, Pageable pageable) {
        return userRepository.findAll(userSpec.hasFilters(filter), pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        String email = userMapper.normalize(request.getEmail());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateRecordException("exceptions.duplicateRecord", "User", "email", email);
        }

        User user = userMapper.toEntity(request);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateStatus(UUID id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exceptions.user.notFound", id));
        user.setStatus(status);
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse setUserInactive(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exceptions.user.notFound", id));
        user.setStatus(UserStatus.INACTIVE);
        return userMapper.toResponse(userRepository.save(user));
    }

}
