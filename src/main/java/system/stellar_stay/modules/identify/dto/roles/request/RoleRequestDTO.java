package system.stellar_stay.modules.identify.dto.roles.request;

import jakarta.persistence.Column;

import java.util.Set;
import java.util.UUID;

public record RoleRequestDTO(
         String code,
         String name,
         String description,

         Set<UUID> permissionIds
) {
}
