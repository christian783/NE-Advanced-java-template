package io.app.my_app.repository;

import io.app.my_app.model.PasswordResetOtp;
import io.app.my_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, UUID> {

    List<PasswordResetOtp> findAllByUserAndUsedAtIsNull(User user);

    Optional<PasswordResetOtp> findTopByUserAndUsedAtIsNullOrderByCreatedAtDesc(User user);
}
