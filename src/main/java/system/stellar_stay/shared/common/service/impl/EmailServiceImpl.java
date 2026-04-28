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
import system.stellar_stay.modules.properties.entity.PropertiesEntity;
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

    @Value("${app.mail.admin}")
    private String adminEmail;

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

    @Override
    public void sendRequestCreatePropertyEmail(PropertiesEntity properties) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(mailFrom);
            helper.setTo(adminEmail);

            Context context = new Context();
            context.setVariable("name", properties.getName());
            context.setVariable("propertyType", properties.getType().name());

            // Format address: address, ward, district, city
            String fullAddress = String.format("%s, %s, %s, %s",
                properties.getAddress(), properties.getWard(),
                properties.getDistrict(), properties.getCity());
            context.setVariable("address", fullAddress);

            String ownerName = "Manager";
            if (properties.getAccount() != null && properties.getAccount().getProfile() != null) {
                ownerName = properties.getAccount().getProfile().getFullName();
            } else if (properties.getAccount() != null) {
                ownerName = properties.getAccount().getEmail();
            }
            context.setVariable("ownerName", ownerName);

            String htmlContent = templateEngine.process("email/request-create-property", context);

            helper.setSubject("Stella Booking - Yêu cầu tạo Property mới: " + properties.getName());
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("[EmailService] Request create property email sent to admin for property: {}", properties.getName());

        } catch (Exception e) {
            log.error("[EmailService] Failed to send request create property email for property: {} error: {}",
                    properties.getName(), e.getMessage());
        }
    }

    @Override
    public void sendResultCreatePropertyEmail(PropertiesEntity properties, String reason) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(mailFrom);

            String targetEmail = "";
            if (properties.getAccount() != null) {
                targetEmail = properties.getAccount().getEmail();
            } else {
                log.warn("[EmailService] Cannot send result email because property account is null for property: {}", properties.getName());
                return;
            }

            helper.setTo(targetEmail);

            Context context = new Context();
            context.setVariable("name", properties.getName());
            context.setVariable("propertyType", properties.getType().name());

            String fullAddress = String.format("%s, %s, %s, %s",
                properties.getAddress(), properties.getWard(),
                properties.getDistrict(), properties.getCity());
            context.setVariable("address", fullAddress);

            String ownerName = "Manager";
            if (properties.getAccount() != null && properties.getAccount().getProfile() != null) {
                ownerName = properties.getAccount().getProfile().getFullName();
            }
            context.setVariable("ownerName", ownerName);

            String htmlContent;
            String subject;

            if (system.stellar_stay.modules.properties.enums.PropertiesStatus.ACTIVE.equals(properties.getStatus())) {
                subject = "Stella Booking - Yêu cầu tạo Property: " + properties.getName() + " ĐÃ ĐƯỢC CHẤP THUẬN";
                htmlContent = templateEngine.process("email/accepted-property", context);
            } else if (system.stellar_stay.modules.properties.enums.PropertiesStatus.REJECTED.equals(properties.getStatus())) {
                subject = "Stella Booking - Yêu cầu tạo Property: " + properties.getName() + " BỊ TỪ CHỐI";
                context.setVariable("reason", reason);
                htmlContent = templateEngine.process("email/denied-property", context);
            } else {
                log.warn("[EmailService] Unknown property status {} for sending result email", properties.getStatus());
                return;
            }

            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("[EmailService] Result create property email sent for property: {}, status: {}", properties.getName(), properties.getStatus());

        } catch (Exception e) {
            log.error("[EmailService] Failed to send result create property email for property: {} error: {}",
                    properties.getName(), e.getMessage());
        }
    }
}
