package system.stellar_stay.modules.identify.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.dto.accounts.response.ProfileInformationResponse;
import system.stellar_stay.modules.identify.dto.login.request.LoginRequestDTO;
import system.stellar_stay.modules.identify.dto.roles.response.RoleInformationResponseDTO;
import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.identify.entity.Profile;
import system.stellar_stay.modules.identify.entity.RefreshToken;
import system.stellar_stay.modules.identify.enums.AccountStatus;
import system.stellar_stay.modules.identify.enums.OTPType;
import system.stellar_stay.modules.identify.mapper.AccountMapper;
import system.stellar_stay.modules.identify.repository.AccountRepository;
import system.stellar_stay.modules.identify.service.*;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.infrastructure.security.CookieService;
import system.stellar_stay.shared.infrastructure.security.JwtTokenProvider;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;
    private final RoleService roleService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final CookieService cookieService;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final OTPService otpService;

    @Override
    public void login(LoginRequestDTO request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Email and password are required");
        }
        String email = request.getEmail().toLowerCase().trim();

        Account account = accountRepository.findByEmail(email);
        if (account == null){
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found with email: " + email);
        }

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Invalid password");
        }

        if (account.getAccountStatus().equals(AccountStatus.INACTIVE)) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account is not active");
        }
        if (account.getAccountStatus().equals(AccountStatus.BANNED)) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account is banned");
        }
        // Lấy role và permisison của account và inject vô token
        Set<String> roles = roleService.findRolesByAccountId(account.getId());
        Set<String> permissions = permissionService.getPermissionsForAccount(account.getId());


        // Generate access and refreshToken
        String accessToken = jwtTokenProvider.generateAccessToken(account.getId(), roles, permissions);
        String refreshToken = refreshTokenService.generateRefreshToken(account.getId(), httpRequest);

        // Set cookie
        cookieService.addAccessTokenCookie(httpResponse, accessToken);
        cookieService.addRefreshTokenCookie(httpResponse, refreshToken);
    }

    @Override
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        // Lấy refresh token từ cookie
            String refreshToken = extractRefreshTokenFromCookie(httpRequest);
            if(refreshToken == null){
                throw new ApiException(ErrorCode.TOKEN_INVALID, "Refresh token not found in cookies");
            }

            // Tìm ra được token rồi thì bắt đầu xóa trong redis, revoke token, xóa cookie
            // Hiện tại thì refreshTokenService đã lo vụ xóa trong redis rồi nhé
            refreshTokenService.revokeRefreshToken(refreshToken);
            // Clear cookie nữa là đẹp
            cookieService.clearCookies(httpResponse);
    }

    @Override
    public void logoutAllDevices(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        // API này dành cho user muốn logout hết tất cả thiết bị của họ

        // Từ request đó, lấy ra accountId từ SecurityContextHolder (đã được filter xác thực trước đó set vào)
        // Từ accountId đó, revoked tất cả các refreshToken của account đó, đồng thời clear cookie của thiết bị hiện tại là được
        // Ví dụ người dùng có qua thiết bị khác thì cũng bị bắt đăng nhập lại bởi vì k có accessToken nào hợp lệ nữa,
        // còn refreshToken thì đã bị revoke hết rồi nên cũng không thể lấy được accessToken mới nữa

        UUID accountId = extractAccountIdFromContextHolder();

        // revoked tất cả refresh token của account đó
        refreshTokenService.revokeAllRefreshToken(accountId);

        //Clear cache permission và role của account đó trong redis để đảm bảo rằng nếu có sự thay đổi về role hoặc permission thì cũng sẽ được cập nhật ngay lập tức
        permissionService.invalidatePermissions(accountId);

        // clear cookie của thiết bị đó
        cookieService.clearCookies(httpResponse);
    }

    @Override
    public void forcedLogout(UUID accountId) {
        // Đối với forcedLogout thì admin chỉ revoked được rToken của user đó thôi, còn lại để TTL của aToken quyết định thgian user còn sử dụng được
        // Nhớ là xóa luôn cả permission của user đó trong redis
        refreshTokenService.revokeAllRefreshToken(accountId);
        permissionService.invalidatePermissions(accountId);
    }

    @Override
    public void sendOTPForForgotPassword(String email) {
        // Ở bước này sẽ check email có tồn tại không -> Gửi otp tới email đó
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Email not found, please register first or check your email again");
        }
        // Gửi OTP tới email đó
        otpService.generateAndSendOTP(email, OTPType.FORGOT_PASSWORD);
    }

    @Override
    public void forgotPasswordWithOTP(String email, String otp, String newPassword) {
        // Bước này là xác thực otp + đổi mật khẩu luôn
        otpService.verifyOTP(email, otp, OTPType.FORGOT_PASSWORD);
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Email not found, please register first or check your email again");
        }
        String newHasedPassword = passwordEncoder.encode(newPassword);
        account.setPassword(newHasedPassword);
        accountRepository.save(account);

        refreshTokenService.revokeAllRefreshToken(account.getId());
        permissionService.invalidatePermissions(account.getId());
    }


    @Override
    public void changePassword(String currentPassword, String newPassword) {
        // Đầu tiên là phải lấy được accountId từ token để chứng minh họ đã đăng nhập rồi
        UUID accountId = extractAccountIdFromContextHolder();
        if(accountId == null){
            throw new ApiException(ErrorCode.UNAUTHENTICATED, "Please login to change password");
        }
        Account account = accountRepository.findById(accountId).orElseThrow( () -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found with ID: " + accountId));
        // flow change password là:
            // Nhập mật khẩu cũ
                // hashed mật khẩu cũ nhập vào để so sánh với DB
                    // Đúng --> lưu mk hashed mới vào DB -> revoke hết refreshToken và cả permission cache trong redis để đảm bảo rằng các token cũ không còn giá trị nữa, phải đăng nhập lại với mk mới
                    // Sai --> trả về lỗi mật khẩu cũ không đúng
        if(!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Current password is incorrect");
        }
        String newHasedPassword = passwordEncoder.encode(newPassword);
        account.setPassword(newHasedPassword);
        accountRepository.save(account);
    }

    @Override
    public ProfileInformationResponse getProfileInformation() {
        //  Lấy accountId từ SecurityContextHolder
        UUID accountId = extractAccountIdFromContextHolder();
        if(accountId == null){
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account ID not found in authentication context");
        }
        // Lấy thông tin tài khoản và profile của account đó
        Account account = accountRepository.findById(accountId).orElseThrow( () -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found"));
        Profile profile= account.getProfile();
        if(profile == null){
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Profile not found");
        }
        Set<RoleInformationResponseDTO> roles = roleService.findAllRolesByAccountIdForAdmin(accountId);

        Set<String> permissions = permissionService.getPermissionsForAccount(accountId);

        ProfileInformationResponse profileInformationResponse = accountMapper.toProfileInformationResponse(account, profile);
        profileInformationResponse.setRoles(roles);
        profileInformationResponse.setPermissions(permissions);

        return profileInformationResponse;
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {

        // Idea là lấy refreshToken từ cookie
        // Từ cái refreshToken đó thì sẽ verify xem token đó có hợp lệ hay không,
        // nếu hợp lệ thì sẽ lấy được accountId từ token đó, còn nếu không hợp lệ thì sẽ trả về lỗi token invalid hoặc expired tùy trường hợp
        // Rồi từ accountId đó, revoked các rToken cũ, generate ra accessToken và refreshToken mới, rồi set cookie mới vào response là được

        String rawRefreshToken = extractRefreshTokenFromCookie(request);
        if(rawRefreshToken == null) {
            throw new ApiException(ErrorCode.TOKEN_INVALID, "Refresh token not found in cookies");
        }
        RefreshToken verifiedRefreshToken = refreshTokenService.verifyRefreshToken(rawRefreshToken);
        UUID accountId = verifiedRefreshToken.getAccount().getId();
        if (accountId == null) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account ID not found in refresh token");
        }
        refreshTokenService.revokeAllRefreshToken(accountId);

        // Sau đó sẽ generate ra accessToken và refreshToken mới, rồi set cookie mới vào response là được
            // Đầu tiên check redis để lấy ra role và permission của người đó
        Set<String> roles = roleService.findRolesByAccountId(accountId);
        Set<String> permissions = permissionService.getPermissionsForAccount(accountId);

        String accessToken = jwtTokenProvider.generateAccessToken(accountId, roles, permissions);
        String refreshToken = refreshTokenService.generateRefreshToken(accountId, request);

        // Set vô cookie là đẹp
        cookieService.addAccessTokenCookie(response, accessToken);
        cookieService.addRefreshTokenCookie(response, refreshToken);
    }





    private UUID extractAccountIdFromContextHolder() {
        // Lấy accountId từ SecurityContextHolder (đã được filter xác thực trước đó)
        // Nếu không có hoặc không hợp lệ thì throw exception
        // Trả về accountId dưới dạng UUID

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Cái authentication này là object được filter xác thực trước đó set vào,
        // chứa thông tin accountId và authorities (roles, permissions)
        // Va cái này được set lúc lọc JwtAuthenticationFilter xác thực accessToken thành công,
        // nếu token hợp lệ thì set authentication vào context holder, còn không thì sẽ không set gì cả
        if(authentication == null){
            throw new ApiException(ErrorCode.UNAUTHENTICATED, "No authentication found in context");
        }
        UUID accountId = (UUID) authentication.getPrincipal();
        if(accountId == null){
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Account ID not found in authentication context");
        }
        return accountId;
    }


    private String extractRefreshTokenFromCookie(HttpServletRequest request){
        if(request.getCookies() == null){
            throw  new ApiException(ErrorCode.TOKEN_INVALID, "Cookies is null");
        }
        // lọc ra refresh token từ một list các token ở trong cookie
        return Arrays.stream(request.getCookies())
                .filter(c -> "refresh_token".equals(c.getName()))
                // Có nghĩa là ly ra các cookie có name là refresh_token,
                // Cái c -> "refresh_token".equals(c.getName()) thì có thể viết đầy đủ ra là:
                // .filter(c -> {
                //     String name = c.getName();
                //     return "refresh_token".equals(name);
                // })
                .map(Cookie::getValue)
                // Sau đó lấy value của cookie đó, tức là token thực sự, chứ không phải tên cookie nữa
                // Có thể viết đầy đủ ra là:
                // .map(c -> {
                //     String value = c.getValue();
                //     return value;
                // })
                .findFirst()// Lấy cái đầu tiên tìm được, vì có thể có nhiều cookie nhưng chỉ cần cái refresh_token đầu tiên là được
                .orElse(null);
    }
}
