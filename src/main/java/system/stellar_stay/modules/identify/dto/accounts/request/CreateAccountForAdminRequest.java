package system.stellar_stay.modules.identify.dto.accounts.request;

import system.stellar_stay.modules.identify.enums.AccountStatus;
import system.stellar_stay.modules.identify.enums.GenderEnum;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record CreateAccountForAdminRequest(
        // account
        String email,
        String password,
        AccountStatus accountStatus,

        // profile
        String idCardNumber,
        String fullName,
        String phoneNumber,
        String avatarUrl,
        GenderEnum gender,
        LocalDate birthDate,
        int loyaltyPoints,

        // role and permission
        Set<UUID> roleIds
) {
}
