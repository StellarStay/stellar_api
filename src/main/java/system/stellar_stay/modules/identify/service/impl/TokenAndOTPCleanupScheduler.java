package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.service.OTPService;
import system.stellar_stay.modules.identify.service.RefreshTokenService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenAndOTPCleanupScheduler {
    private final RefreshTokenService refreshTokenService;
    private final OTPService otpService;

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredRefreshTokens() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
//        Bởi vì refresh token có thời gian sống là 30 ngày,
//        nên cứ sau 24h thì mình sẽ xóa tất cả các refresh token đã tồn tại trước thời điểm hiện tại 30 ngày,
//        tức là những refresh token đã tồn tại hơn 30 ngày, tức là đã hết hạn.
        int deletedCount = refreshTokenService.cleanupExpiredRefreshTokens(threshold);
        log.info("Cleanup expired refresh tokens for {} days", deletedCount);
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void cleanupExpiredOTPs() {
        LocalDateTime threshold = LocalDateTime.now();
        // Có nghĩa là tất cả các OTP đã được tạo ra trước thời điểm này sẽ được coi là hết hạn và sẽ bị xóa khỏi hệ thống.
//      Bởi vì tất cả otp mới tạo thì đều sẽ có thời gian sống là 5 phút,
//      nên cứ sau 30 phút thì mình sẽ xóa tất cả các otp đã tồn tại trước thời điểm hiện tại, tức là những otp đã tồn tại hơn 5 phút, tức là đã hết hạn.
        int deletedCount = otpService.cleanupExpiredOtp(threshold);
        log.info("Cleanup expired OTPs for {} minutes", deletedCount);

    }
}
