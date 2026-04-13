package system.stellar_stay.modules.identify.dto.accounts.response;

import system.stellar_stay.modules.identify.enums.AccountStatus;
import system.stellar_stay.modules.identify.enums.GenderEnum;

import java.time.LocalDate;

public record AccountForAdminResponse(
        String email,
        String password,
        AccountStatus accountStatus,
        String idCardNumber,
        String fullName,
        String phoneNumber,
        String avatarUrl,
        GenderEnum gender,
        LocalDate birthDate,
        int loyaltyPoints
) {
}
