package system.stellar_stay.modules.identify.dto.accounts.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import system.stellar_stay.modules.identify.dto.roles.response.RoleInformationResponseDTO;
import system.stellar_stay.modules.identify.enums.GenderEnum;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileInformationResponse {
    private UUID accountId;
    private String email;
    private String idCardNumber;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private GenderEnum gender;
    private LocalDate birthDate;
    private int loyaltyPoints;
    private Set<RoleInformationResponseDTO> roles;
    private Set<String> permissions;
}
