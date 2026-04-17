package system.stellar_stay.modules.identify.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.identify.entity.RefreshToken;
import system.stellar_stay.modules.identify.repository.AccountRepository;
import system.stellar_stay.modules.identify.repository.RefreshTokenRepository;
import system.stellar_stay.modules.identify.service.AccountService;
import system.stellar_stay.modules.identify.service.RefreshTokenService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.infrastructure.caches.helpers.RedisSupported;
import system.stellar_stay.shared.infrastructure.caches.keys.RedisKeys;
import system.stellar_stay.shared.infrastructure.security.JwtProperties;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;
    private final RedisSupported redisSupported;
    private final JwtProperties jwtProperties;


    @Override
    public String generateRefreshToken(UUID accountId, HttpServletRequest request) {
        if (accountId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID cannot be null");
        }

        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String tokenPlaintext = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

//        Hash before add to DB and Redis
        String tokenHashed = hashRefreshToken(tokenPlaintext);

//        Get information about device and ip of client
        String deviceName = parseDeviceName(request.getHeader("User-Agent"));
        String ipAddress = getClientIp(request);

//        Get account reference
        Account account = accountRepository.getReferenceById(accountId);
//        Save to database
        RefreshToken refreshToken = RefreshToken.builder()
                .account(account)
                .refreshToken(tokenHashed)
                .deviceName(deviceName)
                .IPAddress(ipAddress)
                .expiredAt(LocalDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpiration()))
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

//        Save to Redis
        String redisKey = RedisKeys.refreshTokenKey(tokenHashed);
        redisSupported.set(redisKey, accountId.toString(), RedisKeys.TTL_REFRESH_TOKEN);
        // Nếu đoạn này mà set như này thì nó sẽ có dạng:
        // "refresh_token:{hashedToken}" -> "{accountId}" thì lúc này sẽ check value dựa trên accountId,
        // nếu có thì sẽ lấy ra được accountId, còn nếu không có thì sẽ trả về null, tức là token không hợp lệ hoặc đã bị thu hồi.

        log.debug("Refresh token generated for account: {}", accountId);
        return tokenPlaintext;
    }

    @Override
    public RefreshToken verifyRefreshToken(String tokenPlaintext) {
        // Tức là từ tokenPlaintext (token gốc chưa hash) thì sẽ hash nó lên,
        // rồi check trong Redis xem có tồn tại hay không,
        // nếu có thì sẽ lấy ra được refresh token rồi trả về
        // Nếu không có trong Redis thì mới check trong database,
        // nếu có thì lấy ra thông tin của refresh token đó để trả về,
        // còn nếu không có thì sẽ trả về lỗi token invalid hoặc expired tùy trường hợp.
        if (tokenPlaintext == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Refresh token cannot be null");
        }
//        Lấy token đã hash để check
        String tokenHashed = hashRefreshToken(tokenPlaintext);

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(tokenHashed);
        if (refreshToken == null) {
            throw new ApiException(ErrorCode.TOKEN_INVALID, "Refresh token is invalid or has been revoked");
        }
        if (refreshToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(ErrorCode.TOKEN_EXPIRED, "Refresh token is invalid or has been revoked");
        }

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public void revokeRefreshToken(String tokenPlaintext) {
        String tokenHashed = hashRefreshToken(tokenPlaintext);

//        Xóa trong Redis
        String redisKey = RedisKeys.refreshTokenKey(tokenHashed);
        redisSupported.delete(redisKey);

//        Xóa trong database
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(tokenHashed);
        if (refreshToken == null) {
            throw new ApiException(ErrorCode.TOKEN_INVALID, "Refresh token is invalid or has been revoked");
        }
        refreshToken.setExpiredAt(LocalDateTime.now());
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void revokeAllRefreshToken(UUID accountId) {

//        Idea là lấy hết tất cả các refreshToken theo accountId, rồi set expiredAt về hiện tại và revoked = true, sau đó save lại vào database,
//        đồng thời xóa hết tất cả các refreshToken đó trong Redis dựa trên token đã hash của chúng, để đảm bảo rằng tất cả các refresh token của account đó đều bị thu hồi và không thể sử dụng được nữa.
        if (accountId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID cannot be null");
        }
//        Get all active refresh token of account from DB
        List<String> listActiveRefreshToken = refreshTokenRepository.findAllByAccountIdAndRevokedFalse(accountId);

//        Delete all them on redis
        listActiveRefreshToken.forEach(token -> {
            // Get key in redis
            String keyRToken = RedisKeys.refreshTokenKey(token);
            redisSupported.delete(keyRToken);
        });

//        Soft delete all active refresh token of account in database
        listActiveRefreshToken.forEach(token -> {
            RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token);
            if (refreshToken != null) {
                refreshToken.setExpiredAt(LocalDateTime.now());
                refreshToken.setRevoked(true);
                refreshTokenRepository.save(refreshToken);
            }
        });

        log.debug("All refresh tokens revoked for account: {}", accountId);
    }

    @Override
    public String hashRefreshToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    @Override
    public int cleanupExpiredRefreshTokens(LocalDateTime threshold) {
        return refreshTokenRepository.deleteByExpiredAtBefore(threshold);
    }


    private String parseDeviceName(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("iPhone")) return "iPhone";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        return "Unknown";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) return ip.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
