package system.stellar_stay.modules.identify.service;

import java.time.LocalDateTime;

public interface OTPService {
    public int cleanupExpiredOtps(LocalDateTime threshold);
}
