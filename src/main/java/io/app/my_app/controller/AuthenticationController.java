package io.app.my_app.controller;

import io.app.my_app.model.domain.ApiWrapper;
import io.app.my_app.model.dtos.auth.AuthenticationRequest;
import io.app.my_app.model.dtos.auth.AuthenticationResponse;
import io.app.my_app.model.dtos.auth.RegisterRequest;
import io.app.my_app.model.dtos.auth.ChangePasswordRequest;
import io.app.my_app.model.dtos.auth.ForgotPasswordRequest;
import io.app.my_app.model.dtos.auth.ResetPasswordRequest;
import io.app.my_app.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication, registration, and password management endpoints")
public class AuthenticationController {

    private final AuthenticationService service;
    private final MessageSource messageSource;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Create a new user account with email, password, and phone number. Password must be at least 8 characters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed - invalid email format, duplicate email, or weak password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<AuthenticationResponse>> register(
            @RequestBody
            @Valid RegisterRequest request
    ) {
        return new ApiWrapper<>(service.register(request), localize("responses.auth.register"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticate user with email and password credentials. Returns JWT access and refresh tokens."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or validation error"),
            @ApiResponse(responseCode = "401", description = "Authentication failed - incorrect password or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<AuthenticationResponse>> authenticate(
            @RequestBody
            @Valid AuthenticationRequest request
    ) throws io.app.my_app.exception.UserLoginException {
        return new ApiWrapper<>(service.authenticate(request), localize("responses.auth.authenticate"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Change user password",
            description = "Change password for the authenticated user. Requires current password verification."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error - passwords don't match or password too weak"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token / incorrect current password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<Void>> changePassword(
            @RequestBody
            @Valid ChangePasswordRequest request,
            @Parameter(hidden = true)
            Principal principal
    ) {
        service.changePassword(request, principal);
        return new ApiWrapper<Void>(null, localize("responses.auth.changePassword"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Request password reset",
            description = "Initiate password reset process. Sends OTP (One-Time Password) to the registered email address."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset OTP sent to email"),
            @ApiResponse(responseCode = "400", description = "Email not found or validation error"),
            @ApiResponse(responseCode = "500", description = "Email service error or internal server error")
    })
    public ResponseEntity<ApiWrapper<Void>> forgotPassword(
            @RequestBody
            @Valid ForgotPasswordRequest request
    ) {
        service.requestPasswordReset(request.getEmail());
        return new ApiWrapper<Void>(null, localize("responses.auth.forgotPassword"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password with OTP",
            description = "Reset user password using the OTP received via email. OTP is single-use and expires after 15 minutes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP or validation error"),
            @ApiResponse(responseCode = "401", description = "OTP expired or already used"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<Void>> resetPassword(
            @RequestBody
            @Valid ResetPasswordRequest request
    ) {
        service.resetPassword(request);
        return new ApiWrapper<Void>(null, localize("responses.auth.resetPassword"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping("/refresh-token")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Refresh access token",
            description = "Obtain a new access token using the refresh token. Refresh token should be provided in Authorization header."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New access token issued"),
            @ApiResponse(responseCode = "401", description = "Invalid, expired, or revoked refresh token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void refreshToken(
            @Parameter(hidden = true)
            HttpServletRequest request,
            @Parameter(hidden = true)
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

    private String localize(String code){
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}