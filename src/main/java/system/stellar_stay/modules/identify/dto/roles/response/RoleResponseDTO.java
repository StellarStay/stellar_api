package system.stellar_stay.modules.identify.dto.roles.response;

import system.stellar_stay.modules.identify.dto.permissions.PermissionGroupResponseDTO;

import java.util.Set;
import java.util.UUID;

public record RoleResponseDTO(
        UUID roleId,
        String code,
        String name,
        String description,
        Set<PermissionGroupResponseDTO> permissions
) {
}
