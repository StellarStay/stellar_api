package system.stellar_stay.shared.common.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import system.stellar_stay.modules.identify.enums.OTPType;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.common.service.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Override
    public void sendOtpEmail(String email, String otp, OTPType otpType, long emailVerifyTtl, long resetPasswordTtl) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(mailFrom);
            helper.setTo(email);

            Context context = new Context();
            context.setVariable("otp", otp);

            String htmlContent;
            String subject;

            if (otpType.equals(OTPType.REGISTERED) || otpType.equals(OTPType.CHANGE_EMAIL)) {
                subject = "Mã OTP xác thực email";
                context.setVariable("validityTime", emailVerifyTtl / 60);
                htmlContent = templateEngine.process("email/email-verify", context);
            }
            else if (otpType.equals(OTPType.CHANGE_PASSWORD) || otpType.equals(OTPType.FORGOT_PASSWORD)) {
                subject = "Mã OTP đặt lại mật khẩu";
                context.setVariable("validityTime", resetPasswordTtl / 60);
                htmlContent = templateEngine.process("email/reset-password", context);
            }
            else {
                throw new ApiException(ErrorCode.OTP_INVALID, "Invalid OTP type");
            }

            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("[EmailService] Email sent status={} to={}", otpType, email);

        } catch (Exception e) {
            log.error("[EmailService] Failed to send email status={} to={} error={}",
                    otpType, email, e.getMessage());
        }
    }
}
