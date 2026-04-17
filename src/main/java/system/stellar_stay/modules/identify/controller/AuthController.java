package system.stellar_stay.modules.identify.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import system.stellar_stay.modules.identify.dto.accounts.response.ProfileInformationResponse;
import system.stellar_stay.modules.identify.dto.login.request.LoginRequestDTO;
import system.stellar_stay.modules.identify.enums.OTPType;
import system.stellar_stay.modules.identify.service.AuthService;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.common.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "API login", description = """
            1. Nhập email, mật khẩu
            2. Nếu đúng -> cookie lưu token
            3. Nếu sai -> trả về lỗi
            """)

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   @RequestBody LoginRequestDTO loginRequestDTO) {
        authService.login(loginRequestDTO, request, response);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Login successful")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }


    @Operation(summary = "API logout", description = """
            Logout ở 1 thiết bị hiện tại của người dùng, không ảnh hưởng đến các thiết bị khác.
            """)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Logout successful")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }


    @Operation(summary = "API logout all device", description = """
            Logout ở tất cả thiết bị của người dùng, bao gồm cả thiết bị hiện tại.
            Sau khi gọi API này, người dùng sẽ bị đăng xuất khỏi tất cả các thiết bị đang đăng nhập.
            """)
    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAllDevice(HttpServletRequest request, HttpServletResponse response) {
        authService.logoutAllDevices(request, response);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Logout successful")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }


    @Operation(summary = "API Force logout all device", description = """
            API này dành cho admin force user logout ở tất cả thiết bị của người dùng, bao gồm cả thiết bị hiện tại.
            Sau khi gọi API này, người dùng sẽ bị đăng xuất khỏi tất cả các thiết bị đang đăng nhập sau khi hết TTL của access token hiện tại. 
            """)
    @PostMapping("/forced-logout-all")
    public ResponseEntity<ApiResponse<Void>> forcedLogoutAllDevice(HttpServletRequest request, @RequestParam("accountId") UUID accountId) {
        authService.forcedLogout(accountId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Force user logout successful")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Operation(summary = "API send OTP for forgot password", description = """
               Người dùng nhâp email đã đăng ký tài khoản và yêu cầu gửi mã OTP để đặt lại mật khẩu.
            """)
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendOTPForgotPassword(HttpServletRequest request, @RequestParam("email") String email) {
        authService.sendOTPForForgotPassword(email);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Send OTP for reset password successful")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Operation(summary = "API verify OTP and save new password", description = """
                Hệ thống sẽ xác thực mã OTP, nếu hợp lệ thì sẽ cập nhật mật khẩu mới cho tài khoản của người dùng.
            """)
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOTPAndSaveNewPassword(HttpServletRequest request,
                                                                         @RequestParam("email") String email,
                                                                         @RequestParam("otp") String otp,
                                                                         @RequestParam("newPassword") String newPassword) {
        authService.forgotPasswordWithOTP(email, otp, newPassword);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Verify OTP and save new password successful")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Operation(summary = "API change password", description = """
               API này dành cho người dùng đã đăng nhập, muốn đổi mật khẩu của mình.
               1. Người dùng nhập mật khẩu hiện tại và mật khẩu mới
               2. Gọi API để cập nhật mật khẩu mới cho tài khoản
            """)
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(HttpServletRequest request, @RequestParam("currentPassword") String currentPassword, @RequestParam("newPassword") String newPassword) {
        authService.changePassword(currentPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Change password successful")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }


    @Operation(summary = "API Refresh Token", description = """
            API này dùng để refresh access token khi access token hiện tại đã hết hạn nhưng refresh token vẫn còn hiệu lực.
            Khi gọi API này, hệ thống sẽ kiểm tra refresh token trong cookie, nếu hợp lệ thì sẽ tạo mới access token và refresh token, 
            đồng thời cập nhật cookie mới cho client.
            """)
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Void>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        authService.refreshToken(request, response);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Refresh token successful")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }


    @GetMapping("/me")
    @Operation(summary = "API get profile information", description = """
            API này dùng để lấy thông tin hồ sơ của người dùng đã đăng nhập, bao gồm cả thông tin tài khoản và thông tin cá nhân.
            Khi gọi API này, hệ thống sẽ kiểm tra access token trong cookie, nếu hợp lệ thì sẽ trả về thông tin hồ sơ của người dùng.
            """)
    public ResponseEntity<ApiResponse<ProfileInformationResponse>> getProfileInformation(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.<ProfileInformationResponse>builder()

                .code(ErrorCode.SUCCESS.getCode())
                .message("Get profile information successful")
                .result(authService.getProfileInformation())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }
}
