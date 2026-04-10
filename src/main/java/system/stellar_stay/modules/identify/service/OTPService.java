package system.stellar_stay.modules.identify.service;

import system.stellar_stay.modules.identify.entity.Account;

import java.time.LocalDateTime;

public interface OTPService {
    public int cleanupExpiredOtp(LocalDateTime threshold);

    public void generateAndSendOTP(String email, String OTPStatus, Account account);
    public void verifyOTP(String email, String otp);
    public void resendOTP(String email);





}
