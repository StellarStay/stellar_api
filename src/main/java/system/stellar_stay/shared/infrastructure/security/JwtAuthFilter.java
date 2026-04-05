package system.stellar_stay.shared.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import system.stellar_stay.modules.identify.service.PermissionService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.common.response.ApiResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final PermissionService permissionService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenFromCookie(request);

        if(token == null){
            filterChain.doFilter(request, response);
            return;
        }
        try{
//            Verify token and extract accountId
            UUID accountId = jwtTokenProvider.extractAccountId(token);

//            Load Permissions from redis or database if not exist in redis, then put into context
            Set<String> permissions = permissionService.getPermissionsForAccount(accountId);

//            Build Authorities
            List<SimpleGrantedAuthority> authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

//            Inject to SecurityContext
//                  Đây là cái bước để Spring Security biết được user đã authenticated và có những quyền gì, để từ đó áp dụng authorization (phân quyền) cho các endpoint.
    //                  Tức flow nó sẽ là: JwtAuthFilter sẽ giải mã token, lấy accountId, lấy permissions,
    //                  rồi tạo một Authentication object (ở đây là UsernamePasswordAuthenticationToken) chứa thông tin đó, và set vào SecurityContextHolder.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(token, null, authorities);
            authentication.setDetails(accountId);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        catch (ApiException e){
//            Nếu token lỗi (hết hạn, sai format, v.v.) thì sẽ trả về lỗi 401 Unauthorized, đồng thời clear cookie để tránh bị lỗi liên tục ở lần sau.
            log.warn("[JwtFilter] Token error: {} path={}",
                    e.getErrorCode().name(), request.getRequestURI());
            clearCookiesOnError(response);
            sendErrorResponse(response, e.getErrorCode(), request.getRequestURI());
            return;
        }

        filterChain.doFilter(request, response); // Tiếp tục chuỗi filter, nếu có lỗi ở trên thì sẽ return trước nên không đến đây được

    }


    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "access_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void clearCookiesOnError(HttpServletResponse response) {
        // Clear access_token cookie
        ResponseCookie clear = ResponseCookie.from("access_token", "")
                .httpOnly(true).path("/").maxAge(0).build();
        response.addHeader(HttpHeaders.SET_COOKIE, clear.toString());
    }


    // Hàm này để trả về lỗi dạng JSON khi token có vấn đề, thay vì trả về HTML mặc định của Spring Security, giúp frontend dễ xử lý hơn.
    private void sendErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode,
            String path) throws IOException {

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .path(path)
                .timestamp(Instant.now())
                .build();

        new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValue(response.getWriter(), body);
    }
}
