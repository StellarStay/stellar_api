package system.stellar_stay.shared.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {


    private final JwtProperties jwtProperties;

    @Value("${app.cookie.secure:false}")
    private boolean secure; // true nếu dùng HTTPS, false nếu dev local HTTP
//    Thằng này quyết định cookie có chỉ gửi qua HTTPS hay không.
//    Ở môi trường production, bạn nên đặt secure=true để tăng cường bảo mật.
//    Còn ở môi trường development, nếu bạn không dùng HTTPS, thì secure=false để cookie vẫn hoạt động.
//    HTTPS sẽ mã hóa dữ liệu truyền đi, giúp ngăn chặn việc cookie bị đánh cắp qua mạng (man-in-the-middle attack).
//    Nếu secure=true mà bạn dùng HTTP, thì cookie sẽ không được gửi đi, dẫn đến lỗi authentication.

    @Value("${app.cookie.same-site:Strict}")
    private String sameSite; // "Strict" hoặc "Lax" tùy nhu cầu (Strict an toàn hơn, Lax tiện hơn)
//    Strict: cookie chỉ gửi khi truy cập trực tiếp từ domain chính (tốt cho bảo mật, nhưng có thể gây lỗi khi gọi API từ frontend ở domain khác)
//    Lax: cookie vẫn gửi khi truy cập từ domain khác (tiện cho frontend gọi API, nhưng có thể kém an toàn hơn nếu có lỗ hổng XSS)

    // ── Access token cookie ────────────────────────────────
    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(jwtProperties.getAccessTokenExpiration())
                .build();
    }

    // ── Refresh token cookie ───────────────────────────────
    // Path giới hạn chỉ /api/v1/auth/refresh, muốn thấy ở mọi nơi thành /
    // → browser chỉ gửi cookie này khi gọi đúng endpoint đó
    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
//                .path("/api/v1/auth/refresh")
                .maxAge(jwtProperties.getRefreshTokenExpiration())
                .build();
    }

    // ── Clear cả 2 cookie (logout) ─────────────────────────
    public ResponseCookie clearAccessTokenCookie() {
        return ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(0)
                .build();
    }

    public ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
//              .path("/api/v1/auth/refresh")
                .maxAge(0)
                .build();
    }

    // ── Helper thêm cookie vào response ───────────────────
    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                createAccessTokenCookie(token).toString());
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                createRefreshTokenCookie(token).toString());
    }

    public void clearCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                clearAccessTokenCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                clearRefreshTokenCookie().toString());
    }

}
