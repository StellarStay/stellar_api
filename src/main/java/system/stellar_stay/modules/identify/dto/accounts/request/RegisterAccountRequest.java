package system.stellar_stay.modules.identify.dto.accounts.request;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import system.stellar_stay.modules.identify.enums.GenderEnum;

import java.time.LocalDate;

public record RegisterAccountRequest(
        String email,
        String password,
        String idCardNumber,
        String fullName,
        String phoneNumber,
        String avatarUrl,
        GenderEnum gender,
        LocalDate birthDate
) {
}
