package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.identify.entity.OTPCode;
import system.stellar_stay.modules.identify.enums.OTPStatus;
import system.stellar_stay.modules.identify.enums.OTPType;
import system.stellar_stay.modules.identify.repository.OTPRepository;
import system.stellar_stay.modules.identify.service.AccountService;
import system.stellar_stay.modules.identify.service.OTPService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.common.service.EmailService;
import system.stellar_stay.shared.infrastructure.caches.helpers.RedisSupported;
import system.stellar_stay.shared.infrastructure.caches.keys.RedisKeys;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private final OTPRepository otpRepository;
    private final RedisSupported redisSupport;
    private final EmailService emailService;
    private final AccountService accountService;

    @Value("${app.otp.email-verify-ttl}")
    private long emailVerifyTtl;

    @Value("${app.otp.reset-password-ttl}")
    private long resetPasswordTtl;

    @Value("${app.otp.rate-limit}")
    private int rateLimit;

    @Override
    public int cleanupExpiredOtp(LocalDateTime threshold) {
        return otpRepository.deleteByExpiredAtBefore(threshold);
    }

    @Override
    public void generateAndSendOTP(String email, OTPType otpType) {

        if(email == null || otpType == null){
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "email or otp type is not null" );
        }

//        Rate limit để tránh việc spam OTP, mỗi email chỉ được phép yêu cầu OTP một số lần nhất định trong một khoảng thời gian nhất định.
        String rateLimitKey = RedisKeys.otpRateLimitKey(email);
        long count = redisSupport.increment(rateLimitKey, RedisKeys.TTL_OTP_RATE_LIMIT);
        if (count > rateLimit) {
            // Tức có nghĩa là nếu count lớn hơn 5, tức là đã yêu cầu OTP quá 5 lần trong vòng 1 giờ, thì sẽ bị chặn và không được phép yêu cầu OTP nữa.
            // Và phải đợi hết thời gian sống của key (1 giờ) thì mới có thể yêu cầu OTP tiếp theo.
            throw new RuntimeException("You have exceeded the maximum number of OTP requests. Please try again later.");
        }

//        Xóa các OTP cũ chưa hết hạn của email này để tránh việc có nhiều OTP cùng tồn tại
//        Ví dụ cùng 1 email, nhưng cùng là 1 tác vụ (ví dụ: email verify), nhưng có thể có nhiều OTP khác nhau,
//        thì sẽ xóa hết các OTP đó đi để chỉ còn lại OTP mới nhất.
        otpRepository.deleteByEmailAndTypeAndStatus(email, otpType, OTPStatus.PENDING);

//        Generate OTP mới, lưu vào database và gửi email cho người dùng
        String otpCode = String.format("%06d", new SecureRandom().nextInt(1000000));

//        Hash lại trước khi lưu
        String hashedOtp = hashOTP(otpCode);

//        Lấy ttl của otp
        long ttl = resolvedTTL(otpType);

//        Lưu theo thứ tự redis -> db để backup
        redisSupport.set(RedisKeys.otpCodeKey(otpType.name(), email), hashedOtp, ttl);

        OTPCode savedOtp = OTPCode.builder()
                .emailVerified(email)
                .status(OTPStatus.PENDING)
                .type(otpType)
                .otpHashed(hashedOtp)
                .expiredAt(LocalDateTime.now().plusSeconds(ttl))
                .build();

        otpRepository.save(savedOtp);

//        Bước cuối cùng là gửi email cho người dùng, sử dụng JavaMailSender để gửi email chứa mã OTP đến địa chỉ email của người dùng.
        emailService.sendOtpEmail(email, otpCode, otpType, emailVerifyTtl, resetPasswordTtl);
    }


    @Override
    public void verifyOTP(String email, String otp, OTPType otpType) {

        // idea để xác thực otp
        // user nhận otp ở gmail -> Nhập otp dô
        // Dưới BE sẽ nhận được otp từ FE, sau đó sẽ hash lại mã otp đó
        // Sau đó BE sẽ bốc ra cái otp đã lưu trong redis ra để so sánh:
            // Nếu redis có tồn tại otp đó -> tiếp tục so sánh otp đã hash với otp đã nằm trong redis
            // Nếu trong redis không tồn tại -> Thử check trong DB để xem OTP có tồn tại không
                // Nếu trong DB cũng không tồn tại -> OTP không hợp lệ
                // Nếu trong DB có tồn tại -> Kiểm tra xem OTP đã hết hạn chưa, nếu chưa hết hạn thì sẽ so sánh otp đã hash với otp đã hash trong DB


        // Hashed lại otp người dùng nhập và tạo key để bóc ra otp đã lưu trong redis
        String hashedOtp = hashOTP(otp);
        String redisKey = RedisKeys.otpCodeKey(otpType.name(), email.toLowerCase()); // Tạo key để truy xuất OTP đã lưu trong Redis.

        // Từ key đó, bóc ra otp đã lưu trong redis
        String redisHashedOtp = redisSupport.get(redisKey);

        // So sánh otp đã hash với otp đã hash trong redis
        if(redisHashedOtp != null){
            if (redisHashedOtp.equals(hashedOtp)) { // Thành công thì xóa trong redis và đồng thời update lại status của OTP trong DB thành VERIFIED
                log.info("[OTP] OTP verified successfully from Redis for email={} and type={}", email, otpType);
                String rateKey = RedisKeys.otpRateLimitKey(email.toLowerCase());
                redisSupport.delete(redisKey);
                redisSupport.delete(rateKey);
                OTPCode otpFound = otpRepository.findByEmailAndTypeAndStatus(email, otpType, OTPStatus.PENDING);
                if (otpFound == null) {
                    throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "OTP not found");
                }
                otpFound.setStatus(OTPStatus.USED);
                otpRepository.save(otpFound);
            }
            else {
                log.warn("[OTP] Invalid OTP attempt from Redis for email={} and type={}", email, otpType);
                throw new ApiException(ErrorCode.OTP_INVALID, "Invalid OTP");
            }
        }
        else{
            // Lúc này là otp không tồn tại trong redis, có thể là do đã hết hạn trong redis hoặc chưa được tạo ra, nên sẽ check tiếp trong DB
            OTPCode otpCode = otpRepository.findByEmailAndTypeAndStatus(email.toLowerCase(), otpType, OTPStatus.PENDING);
            // Check các điểu kiện:
                // OTP không tồn tại
                // OTP hết hạn
                // OTP có trạng thái là USED hoặc EXPIRED
                // OTP có equal với otp hashed ở trên ko

            if (otpCode == null){
                throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "OTP not found");
            }
            else if (otpCode.getStatus().equals(OTPStatus.USED)) {
                throw new ApiException(ErrorCode.OTP_EXPIRED, "OTP is used");
            }
            else if (otpCode.getExpiredAt().isBefore(LocalDateTime.now())) {
                throw new  ApiException(ErrorCode.OTP_EXPIRED, "OTP is expired");
            }
            else if (!otpCode.getOtpHashed().equals(hashedOtp)) {
                throw new ApiException(ErrorCode.OTP_INVALID, "Invalid OTP");
            }

            // Lấy account liên quan đến OTP đó để set vô
            Account account = accountService.findAccountByEmail(email);
            if(account != null){
                otpCode.setAccount(account);
            }
            otpCode.setStatus(OTPStatus.USED);
            otpRepository.save(otpCode);
        }


    }

    @Override
    public void resendOTP(String email, OTPType otpType) {
        // Gọi lại hàm generate and send email là được
        generateAndSendOTP(email, otpType);
    }


//    Làm quả hàm Helpers để xử lý mấy cái việc như hash otp,...

    private long resolvedTTL(OTPType type) {
        if (type.equals(OTPType.REGISTERED) || type.equals(OTPType.CHANGE_EMAIL)) {
            return emailVerifyTtl;
        }
        else if (type.equals(OTPType.CHANGE_PASSWORD) || type.equals(OTPType.FORGOT_PASSWORD)) {
            return resetPasswordTtl;
        }
        else {
            throw new ApiException(ErrorCode.OTP_INVALID, "Invalid OTP status");
        }
    }


    //    Làm quả hàm để hashed code
    private String hashOTP(String otp) {
        // Sử dụng một thuật toán hash đơn giản, ví dụ: SHA-256
        // Thuật toán hash sẽ biến đổi mã OTP thành một chuỗi khác, giúp bảo mật hơn khi lưu trữ trong database.
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Tạo một instance của MessageDigest với thuật toán SHA-256
            // Instance là một đối tượng có thể sử dụng để thực hiện các phép toán hash, trong trường hợp này là hash mã OTP.
            byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8)); // Chuyển đổi chuỗi OTP thành mảng byte và hash nó
            return Base64.getEncoder().encodeToString(hash);
            // cái base64 này để mã hóa lại chuỗi hash thành dạng dễ lưu trữ và so sánh hơn,
            // vì hash thường sẽ trả về một mảng byte, mà chúng ta muốn lưu trữ dưới dạng String.
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

}
