package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.repository.OTPRepository;
import system.stellar_stay.modules.identify.service.OTPService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private final OTPRepository otpRepository;

    @Override
    public int cleanupExpiredOtps(LocalDateTime threshold) {
        return otpRepository.deleteByExpiredAtBefore(threshold);
    }
}
