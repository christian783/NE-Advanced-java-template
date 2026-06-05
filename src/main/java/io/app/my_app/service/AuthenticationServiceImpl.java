package io.app.my_app.service;

import io.app.my_app.mapper.AuthMapper;
import io.app.my_app.model.Token;
import io.app.my_app.model.User;
import io.app.my_app.model.dtos.auth.AuthenticationRequest;
import io.app.my_app.model.dtos.auth.AuthenticationResponse;
import io.app.my_app.model.dtos.auth.ChangePasswordRequest;
import io.app.my_app.model.dtos.auth.RegisterRequest;
import io.app.my_app.model.dtos.auth.ResetPasswordRequest;
import io.app.my_app.model.enums.TokenType;
import io.app.my_app.model.enums.Role;
import io.app.my_app.model.enums.UserStatus;
import io.app.my_app.repository.TokenRepository;
import io.app.my_app.repository.UserRepository;
import io.app.my_app.repository.PasswordResetOtpRepository;
import io.app.my_app.exception.DuplicateRecordException;
import io.app.my_app.exception.ResourceNotFoundException;
import io.app.my_app.exception.BadRequestException;
import io.app.my_app.exception.UserLoginException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import io.app.my_app.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetOtpRepository otpRepository;
    private final MessageSource messageSource;
    private final MailService mailService;
    private final AuthMapper authMapper;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        // check for duplicate email
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateRecordException("exceptions.duplicateRecord", "User", "email", request.getEmail());
        }

        var user = authMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);

        var savedUser = repository.save(user);

        var jwtToken = jwtService.generateToken(user);

        var refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);

        // Send welcome email
        try {
            mailService.sendHtmlEmail(
                    savedUser.getEmail(),
                    "Welcome to MyApp!",
                    "email/welcome",
                    java.util.Map.of("userName", savedUser.getFullName())
            );
        } catch (Exception e) {
            log.warn("Failed to send welcome email to {}", savedUser.getEmail(), e);
        }

        return authMapper.toResponse(jwtToken, refreshToken);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) throws UserLoginException {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (org.springframework.security.core.AuthenticationException ex) {
            // convert to application specific exception so global handler maps it properly
            throw new UserLoginException(messageSource.getMessage("exceptions.invalid.credentials", null, "Invalid credentials", LocaleContextHolder.getLocale()));
        }

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("exceptions.notFound", "User", "email"));

        var jwtToken = jwtService.generateToken(user);

        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);

        saveUserToken(user, jwtToken);

        return authMapper.toResponse(jwtToken, refreshToken);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = authMapper.toResponse(accessToken, refreshToken);

                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }

        }
    }

    @Override
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("exceptions.invalid.current.password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new BadRequestException("exceptions.passwords.mismatch");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

    @Override
    public void requestPasswordReset(String email) {
        var user = repository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("exceptions.notFound", "User", "email"));

        // generate a numeric OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit
        String hashed = passwordEncoder.encode(otp);

        var expiry = java.time.LocalDateTime.now().plusMinutes(15);

        var otpEntity = io.app.my_app.model.PasswordResetOtp.builder()
                .user(user)
                .otpHash(hashed)
                .expiresAt(expiry)
                .build();

        otpRepository.save(otpEntity);

        // Send OTP to user's email using Thymeleaf template
        try {
            mailService.sendHtmlEmail(
                    user.getEmail(),
                    "Reset Your Password",
                    "email/password-reset",
                    java.util.Map.of(
                            "userName", user.getFullName(),
                            "otp", otp,
                            "expiryMinutes", 15
                    )
            );
        } catch (Exception e) {
            // Log error but don't fail the request
            log.warn("Failed to send password reset OTP email to {}", user.getEmail(), e);
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        var user = repository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("exceptions.notFound", "User", "email"));

        var otpOpt = otpRepository.findTopByUserAndUsedAtIsNullOrderByCreatedAtDesc(user);
        if (otpOpt.isEmpty()) {
            throw new BadRequestException("exceptions.invalid.otp");
        }

        var otpEntity = otpOpt.get();

        if (otpEntity.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new BadRequestException("exceptions.otp.expired");
        }

        if (!passwordEncoder.matches(request.getOtp(), otpEntity.getOtpHash())) {
            throw new BadRequestException("exceptions.invalid.otp");
        }

        // mark used
        otpEntity.setUsedAt(java.time.LocalDateTime.now());
        otpRepository.save(otpEntity);

        // set new password
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new BadRequestException("exceptions.passwords.mismatch");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
    }



}
