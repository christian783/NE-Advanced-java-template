package io.app.my_app.service;

import io.app.my_app.model.dtos.user.UserCreateRequest;
import io.app.my_app.model.dtos.user.UserFilter;
import io.app.my_app.model.dtos.user.UserResponse;
import io.app.my_app.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserManagementService {
    Page<UserResponse> findAll(UserFilter filter, Pageable pageable);

    UserResponse create(UserCreateRequest request);

    UserResponse updateStatus(UUID id, UserStatus status);

    UserResponse setUserInactive(UUID id);
}
