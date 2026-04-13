package system.stellar_stay.shared.common.service;

import system.stellar_stay.modules.identify.enums.OTPType;

public interface EmailService {
    void sendOtpEmail(String email, String otp, OTPType otpType, long emailVerifyTtl, long resetPasswordTtl);
}
