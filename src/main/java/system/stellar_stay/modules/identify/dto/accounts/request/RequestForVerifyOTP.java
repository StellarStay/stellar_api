package system.stellar_stay.modules.identify.dto.accounts.request;

public record RequestForVerifyOTP(
        String email,
        String otp
) {
}
