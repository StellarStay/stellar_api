package system.stellar_stay.shared.common.service;

import system.stellar_stay.modules.identify.enums.OTPType;
import system.stellar_stay.modules.properties.entity.PropertiesEntity;

public interface EmailService {
    void sendOtpEmail(String email, String otp, OTPType otpType, long emailVerifyTtl, long resetPasswordTtl);

    void sendRequestCreatePropertyEmail(PropertiesEntity properties);

    void sendResultCreatePropertyEmail(PropertiesEntity properties, String reason);
}
