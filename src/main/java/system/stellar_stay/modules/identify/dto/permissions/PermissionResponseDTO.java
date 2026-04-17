package system.stellar_stay.modules.identify.dto.permissions;

import java.util.UUID;

public record PermissionResponseDTO(
        UUID permissionId,
        String name,
        String description
) {
}
