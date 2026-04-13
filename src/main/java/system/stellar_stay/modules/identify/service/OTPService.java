package system.stellar_stay.modules.identify.service;

import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.identify.enums.OTPStatus;
import system.stellar_stay.modules.identify.enums.OTPType;

import java.time.LocalDateTime;

public interface OTPService {
    public int cleanupExpiredOtp(LocalDateTime threshold);

    public void generateAndSendOTP(String email, OTPType otpType);
    public void verifyOTP(String email, String otp, OTPType otpType);
    public void resendOTP(String email, OTPType otpType);





}
