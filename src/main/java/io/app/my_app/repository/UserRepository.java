package io.app.my_app.repository;

import io.app.my_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaSpecificationExecutor<User>, JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

}