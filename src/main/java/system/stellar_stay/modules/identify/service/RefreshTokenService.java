package system.stellar_stay.modules.identify.service;

import jakarta.servlet.http.HttpServletRequest;
import system.stellar_stay.modules.identify.entity.RefreshToken;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RefreshTokenService {
    public String generateRefreshToken(UUID accountId, HttpServletRequest request);
    public RefreshToken verifyRefreshToken(String tokenPlaintext);
    public void revokeRefreshToken(String tokenPlaintext);
    public void revokeAllRefreshToken(UUID accountId);
    public String hashRefreshToken(String token);

    public int cleanupExpiredRefreshTokens(LocalDateTime threshold);
}
