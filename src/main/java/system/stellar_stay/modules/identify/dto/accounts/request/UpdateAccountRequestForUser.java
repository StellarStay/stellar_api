package system.stellar_stay.modules.identify.dto.accounts.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import system.stellar_stay.modules.identify.enums.GenderEnum;

import java.time.LocalDate;

public record UpdateAccountRequestForUser(
        @Email(message = "Email không đúng định dạng")
        String email,
        String idCardNumber,
        String fullName,

        @Pattern(regexp = "^(\\+84|0)[0-9]{9}$",
                message = "Số điện thoại không đúng định dạng")
        String phoneNumber,
        String avatarUrl,
        GenderEnum gender,
        LocalDate birthDate
) {
}
