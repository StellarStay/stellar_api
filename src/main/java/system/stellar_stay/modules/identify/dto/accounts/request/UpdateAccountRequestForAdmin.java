package system.stellar_stay.modules.identify.dto.accounts.request;

import system.stellar_stay.modules.identify.enums.AccountStatus;
import system.stellar_stay.modules.identify.enums.GenderEnum;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateAccountRequestForAdmin(
        // account
        String email,

        // profile
        AccountStatus accountStatus,
        String idCardNumber,
        String fullName,
        String phoneNumber,
        String avatarUrl,
        GenderEnum gender,
        LocalDate birthDate,
        int loyaltyPoints,

        // role and permission
        UUID roleId
) {
}
