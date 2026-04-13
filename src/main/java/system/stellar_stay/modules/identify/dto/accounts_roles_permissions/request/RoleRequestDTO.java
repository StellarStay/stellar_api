package system.stellar_stay.modules.identify.dto.accounts_roles_permissions.request;

import jakarta.persistence.Column;

public record RoleRequestDTO(
         String code,
         String name,
         String description
) {
}
