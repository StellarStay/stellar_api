package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.identify.entity.OTPCode;
import system.stellar_stay.modules.identify.repository.OTPRepository;
import system.stellar_stay.modules.identify.service.OTPService;
import system.stellar_stay.shared.infrastructure.caches.helpers.RedisSupported;
import system.stellar_stay.shared.infrastructure.caches.keys.RedisKeys;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private final OTPRepository otpRepository;
    private final RedisSupported redisSupport;
    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Value("${app.otp.email-verify-ttl}")
    private long emailVerifyTtl;

    @Value("${app.otp.reset-password-ttl}")
    private long resetPasswordTtl;

    @Value("${app.otp.rate-limit}")
    private int rateLimit;

    @Override
    public int cleanupExpiredOtp(LocalDateTime threshold) {
        return otpRepository.deleteByExpiredAtBefore(threshold);
    }

    @Override
    public void generateAndSendOTP(String email, String OTPStatus, Account account) {
//        Rate limit để tránh việc spam OTP, mỗi email chỉ được phép yêu cầu OTP một số lần nhất định trong một khoảng thời gian nhất định.
        String rateLimitKey = RedisKeys.otpRateLimitKey(email);
        long count = redisSupport.increment(rateLimitKey, 1);
        if (count == 1) {
            redisSupport.increment(rateLimitKey, RedisKeys.TTL_OTP); // Đặt thời gian sống cho key là 1 giờ (3600 giây)
        }
        if (count > rateLimit) {
            // Tức có nghĩa là nếu count lớn hơn 5, tức là đã yêu cầu OTP quá 5 lần trong vòng 1 giờ, thì sẽ bị chặn và không được phép yêu cầu OTP nữa.
            // Và phải đợi hết thời gian sống của key (1 giờ) thì mới có thể yêu cầu OTP tiếp theo.
            throw new RuntimeException("You have exceeded the maximum number of OTP requests. Please try again later.");
        }

//        Xóa các OTP cũ chưa hết hạn của email này để tránh việc có nhiều OTP cùng tồn tại
        otpRepository.deleteByEmailAndStatus(email, OTPStatus);




    }

    @Override
    public void verifyOTP(String email, String otp) {

    }

    @Override
    public void resendOTP(String email) {

    }
}
