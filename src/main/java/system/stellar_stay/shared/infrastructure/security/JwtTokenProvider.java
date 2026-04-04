package system.stellar_stay.shared.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                Base64.getEncoder().encodeToString(
                        HexFormat.of().parseHex(jwtProperties.getSecretKey())
                )
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ── Generate access token ──────────────────────────────
    public String generateAccessToken(UUID accountId, Set<String> permissions) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(accountId.toString())
                .claim("permissions", permissions)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(jwtProperties.getAccessTokenExpiration())))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Verify + parse ─────────────────────────────────────
    public Claims verifyAndExtractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new ApiException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new ApiException(ErrorCode.TOKEN_INVALID);
        }
    }

//    Thằng cu này sẽ giải mã token để lấy ra accountId,
//    sau đó sẽ dùng accountId này để truy xuất permissions từ Redis thay vì phải giải mã token nhiều lần
    public UUID extractAccountId(String token) {
        return UUID.fromString(verifyAndExtractClaims(token).getSubject());
    }

//    Đây là nơi sẽ làm nhiệm vụ giải mã token để lấy ra permissions,
//    sau đó sẽ lưu vào Redis để tiện truy xuất sau này mà không cần phải giải mã token nhiều lần
    @SuppressWarnings("unchecked")
    public Set<String> extractPermissions(String token) {
        Object perms = verifyAndExtractClaims(token).get("permissions");
        if (perms instanceof List<?> list) {
            return new HashSet<>((List<String>) list);
        }
        return Set.of();
    }

}
