package system.stellar_stay.modules.identify.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import system.stellar_stay.modules.identify.dto.accounts.request.*;
import system.stellar_stay.modules.identify.dto.accounts.response.AccountForAdminResponse;
import system.stellar_stay.modules.identify.dto.accounts.response.AccountForUserResponse;
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


    // API For User
    // 1. Register

    @Operation(summary = "API register account", description = """
             Flow nó đi sẽ là:
             - User nhập thông tin đăng kí tài khoản --> FE gọi API register account với thông tin đăng kí tài khoản
             - Nếu thông tin đăng kí hợp lệ thì sẽ tạo tài khoản thành công
             - Nhưng lúc này tài khoản vẫn đang ở trạng thái INACTIVE
             - Nên lúc này FE sẽ gọi API verify OTP để xác thực email,
                nếu xác thực thành công thì tài khoản sẽ được chuyển sang trạng thái ACTIVE và có thể đăng nhập vào hệ thống, 
                còn nếu xác thực thất bại thì sẽ trả về lỗi và yêu cầu nhập lại OTP
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

    @Operation(summary = "API verify for account", description = """
             API này dùng để xác thực tài khoản sau khi đăng kí, 
             vì sau khi đăng kí thì tài khoản vẫn đang ở trạng thái INACTIVE 
             nên cần phải xác thực email để chuyển sang trạng thái ACTIVE và có thể đăng nhập vào hệ thống được.
            """)
    @PostMapping("/user/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyAccount(HttpServletRequest request,
                                                           @RequestBody RequestForVerifyOTP requestForVerifyOTP) {
        accountService.verifyRegisterAccount(requestForVerifyOTP.email(), requestForVerifyOTP.otp());
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Register account successfully, please check your email to verify your account")
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


    // API For Admin

    // 1. Create account for admin
    @Operation(summary = "API create account for admin", description = """
            Dùng để tạo tài khoản cho admin:
            1. Admin nhập thông tin tài khoản mới vào form tạo tài khoản
            2. FE gọi API create account for admin với thông tin tài khoản mới để tạo tài khoản
            3. Nếu tạo thành công thì trả về thông tin tài khoản đã được tạo, c��n nếu tạo thất bại thì trả về lỗi và yêu cầu nhập lại thông tin
            """)
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<AccountForAdminResponse>> createAccountForAdmin(HttpServletRequest request,
                                                                                      @RequestBody CreateAccountForAdminRequest createAccountForAdminRequest) {
        return ResponseEntity.ok(
                ApiResponse.<AccountForAdminResponse>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Create account for admin successfully")
                        .result(accountService.createAccountForAdmin(createAccountForAdminRequest))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    // 2. Update account information for admin
    @Operation( summary = "API update account for admin", description = """
            Dùng để cập nhật thông tin tài khoản cho admin:
            1. Admin nhập thông tin mới vào form cập nhật thông tin tài khoản
            2. FE gọi API update account for admin với accountId của admin và thông tin mới để cập nhật tài khoản
            3. Nếu cập nhật thành công thì trả về thông tin tài khoản đã được cập nhật, còn nếu cập nhật thất bại thì trả về lỗi và yêu cầu nhập lại thông tin
            """)

    @PutMapping("/admin/{accountId}")
    public ResponseEntity<ApiResponse<AccountForAdminResponse>> updateAccountForAdmin(HttpServletRequest request,
                                                                                      @PathVariable("accountId") UUID accountId,
                                                                                      @RequestBody UpdateAccountRequestForAdmin updateAccountRequestForAdmin) {
        return ResponseEntity.ok(
                ApiResponse.<AccountForAdminResponse>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Create account for admin successfully")
                        .result(accountService.updateAccountForAdmin(accountId, updateAccountRequestForAdmin))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // 3. Inactive account for admin
    @Operation(summary = "API inActive account for admin", description = """
            Dùng để vô hiệu hóa tài khoản cho admin:
            1. Admin bấm nút vô hiệu hóa tài khoản
            2. FE gọi API inActive account for admin với accountId của admin để vô hiệu hóa tài khoản
            3. Nếu vô hiệu hóa thành công thì trả về thông báo vô hiệu hóa thành công, còn nếu vô hiệu hóa thất bại thì trả về lỗi và yêu cầu thử lại sau
            """)
    @DeleteMapping("/admin/{accountId}/inactive")
    public ResponseEntity<ApiResponse<Void>> inActiveAccountForAdmin(HttpServletRequest request, @PathVariable("accountId") UUID accountId) {
        accountService.inActiveAccountForAdmin(accountId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("InActive account for admin successfully")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // 4. BAN account for admin
    @Operation(summary = "API ban account for admin", description = """
            Dùng để ban tài khoản cho admin:
            1. Admin bấm nút ban tài khoản
            2. FE gọi API ban account for admin với accountId của admin để ban tài khoản
            3. Nếu ban thành công thì trả về thông báo ban thành công, còn nếu ban thất bại thì trả về lỗi và yêu cầu thử lại sau
            """)
    @DeleteMapping("/admin/{accountId}/ban")
    public ResponseEntity<ApiResponse<Void>> banAccountForAdmin(HttpServletRequest request, @PathVariable("accountId") UUID accountId) {
        accountService.banAccountForAdmin(accountId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Ban account for admin successfully")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }



    // 5. Get all accounts for admin
    @Operation(summary = "API get all accounts for admin", description = """
            Dùng để lấy tất cả tài khoản cho admin:
            1. Admin nhập thông tin tìm kiếm vào form tìm kiếm tài khoản (có thể để trống để lấy tất cả tài khoản)
            2. FE gọi API get all accounts for admin với thông tin tìm kiếm để lấy tất cả tài khoản phù hợp với thông tin tìm kiếm
            3. Nếu lấy thành công thì trả về danh sách tài khoản phù hợp với thông tin tìm kiếm, còn nếu lấy thất bại thì trả về lỗi và yêu cầu thử lại sau
            """)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<AccountForAdminResponse>>> getAllAccountsForAdmin(HttpServletRequest request,
                                                                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                             @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                             @RequestParam(value = "sortBy", defaultValue = "createdAt") String sort,
                                                                                             @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                                                                                             @RequestParam(value = "keyword", required = false) String keyword) {

        return ResponseEntity.ok(
                ApiResponse.<Page<AccountForAdminResponse>>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Get all accounts for admin successfully")
                        .result(accountService.getAllAccountsForAdmin(page, size, sort, sortDir, keyword))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
