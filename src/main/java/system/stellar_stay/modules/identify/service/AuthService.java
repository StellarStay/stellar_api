package system.stellar_stay.modules.identify.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import system.stellar_stay.modules.identify.dto.accounts.response.ProfileInformationResponse;
import system.stellar_stay.modules.identify.dto.login.request.LoginRequestDTO;
import system.stellar_stay.modules.identify.enums.OTPType;

import java.util.UUID;

public interface AuthService {
    void login(LoginRequestDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse);
    void logoutAllDevices(HttpServletRequest httpRequest, HttpServletResponse httpResponse  );
    void forcedLogout(UUID accountId);

    void sendOTPForForgotPassword(String email);
    void forgotPasswordWithOTP(String email, String otp, String newPassword);

    void changePassword(String currentPassword, String newPassword);

    ProfileInformationResponse getProfileInformation();

    void refreshToken(HttpServletRequest request, HttpServletResponse response);


}
