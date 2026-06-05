package io.app.my_app.service;

import io.app.my_app.exception.UserLoginException;
import io.app.my_app.model.dtos.auth.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Principal;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request) throws UserLoginException;

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    void requestPasswordReset(String email);

    void resetPassword(ResetPasswordRequest request);
}
