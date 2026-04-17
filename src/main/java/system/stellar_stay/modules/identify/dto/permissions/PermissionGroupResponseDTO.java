package system.stellar_stay.modules.identify.dto.permissions;

import java.util.List;
import java.util.Set;

public record PermissionGroupResponseDTO(
        String resource,
        List<PermissionResponseDTO> permissions
) {
}
