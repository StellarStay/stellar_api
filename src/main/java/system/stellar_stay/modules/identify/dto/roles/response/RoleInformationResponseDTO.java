package system.stellar_stay.modules.identify.dto.roles.response;

import java.util.UUID;

public record RoleInformationResponseDTO(
        UUID roleId,
        String code,
        String name,
        String description
) {
}
