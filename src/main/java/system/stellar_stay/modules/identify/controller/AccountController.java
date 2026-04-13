package system.stellar_stay.modules.identify.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import system.stellar_stay.modules.identify.dto.accounts.request.RegisterAccountRequest;
import system.stellar_stay.modules.identify.dto.accounts.request.UpdateAccountRequestForUser;
import system.stellar_stay.modules.identify.dto.accounts.response.AccountForUserResponse;
import system.stellar_stay.modules.identify.enums.OTPType;
import system.stellar_stay.modules.identify.service.AccountService;
import system.stellar_stay.modules.identify.service.OTPService;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.common.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

    private final OTPService otpService;
    private final AccountService accountService;


    @Operation(summary = "API send otp", description = """
            Dùng để send otp về email:
            1. Nhập email
            2. Gửi otp về email
            """)

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(HttpServletRequest request,
                                                         @RequestParam("email") String email,
                                                         @RequestParam("otp-type") OTPType otpType) {
        otpService.generateAndSendOTP(email, otpType);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("OTP sent to your email, please check and verify")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Operation(summary = "API verify email", description = """
            Dùng để verify email:
            1. FE truyền email kèm với OTP và type của OTP (REGISTER, FORGOT_PASSWORD, CHANGE_EMAIL) để xác thực email
            2. Nếu OTP hợp lệ thì sẽ xác thực thành công, còn nếu OTP không hợp lệ thì sẽ trả về lỗi và yêu cầu nhập lại OTP
            4. Quay về login nếu xác thực đúng
            """)

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(HttpServletRequest request,
                                                       @RequestParam("email") String email,
                                                       @RequestParam("otp") String otp,
                                                       @RequestParam("otp-type") OTPType otpType) {

        otpService.verifyOTP(email, otp, otpType);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Verify successfully, please login again")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // API For User

    // 1. Register

    @Operation(summary = "API register account", description = """
             Flow nó đi sẽ là:
             - User nhập thông tin đăng kí tài khoản --> Bấm nút xác thực email -> FE tạm lưu trên localStorage hay gì đó
             - FE gọi API send-otp và lấy email lúc nãy kèm với type là REGISTER để gửi OTP về email
             - User nhận được OTP và nhập vào form xác thực OTP -> FE gọi API verify-otp với email, OTP và type là REGISTER để xác thực
             - Nếu xác thực thành công thì FE sẽ gọi API register để tạo tài khoản với thông tin đã nhập lúc đầu đã được lưu tạm trên FE,
             - Còn nếu xác thực thất bại thì FE sẽ hiện thông báo lỗi và yêu cầu nhập lại OTP
            """)
    @PostMapping("/user/register")
    public ResponseEntity<ApiResponse<AccountForUserResponse>> registerAccount(HttpServletRequest request,
                                                                               @RequestBody RegisterAccountRequest registerAccountRequest) {
        return ResponseEntity.ok(
                ApiResponse.<AccountForUserResponse>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Register account successfully, please check your email to verify your account")
                        .result(accountService.registerAccount(registerAccountRequest))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    // 2. Update Account

    @Operation(summary = "API update account", description = """
            Dùng để update thông tin tài khoản của user:
            1. User nhập thông tin mới vào form cập nhật thông tin tài khoản
            2. FE gọi API update account với accountId của user và thông tin mới để cập nhật tài khoản
            3. Nếu cập nhật thành công thì trả về thông tin tài khoản đã được cập nhật, còn nếu cập nhật thất bại thì trả về lỗi và yêu cầu nhập lại thông tin
            """)
    @PutMapping("/user/{accountId}")
    public ResponseEntity<ApiResponse<AccountForUserResponse>> updateAccount(HttpServletRequest request,
                                                                             @RequestParam("accountId") UUID accountId,
                                                                             @RequestBody UpdateAccountRequestForUser updateAccountRequestForUser) {

        return ResponseEntity.ok(
                ApiResponse.<AccountForUserResponse>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Update account successfully")
                        .result(accountService.updateAccount(accountId, updateAccountRequestForUser))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }



    // 3. Delete Account
    @Operation(summary = "API delete account", description = """
            Dùng để xóa tài khoản của user:
            1. User bấm nút xóa tài khoản
            2. FE gọi API delete account với accountId của user để xóa tài khoản
            3. Nếu xóa thành công thì trả về thông báo xóa thành công, còn nếu xóa thất bại thì trả về lỗi và yêu cầu thử lại sau
            """)

    @DeleteMapping("/user/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(HttpServletRequest request, @RequestParam("accountId") UUID accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Delete account successfully")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
