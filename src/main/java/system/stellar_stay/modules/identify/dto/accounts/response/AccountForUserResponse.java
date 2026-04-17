package system.stellar_stay.modules.identify.dto.accounts.response;

import system.stellar_stay.modules.identify.enums.GenderEnum;

import java.time.LocalDate;
import java.util.UUID;

public record AccountForUserResponse(
        UUID accountId,
        String email,
        String idCardNumber,
        String fullName,
        String phoneNumber,
        String avatarUrl,
        GenderEnum gender,
        LocalDate birthDate,
        int loyaltyPoints
) {
}
