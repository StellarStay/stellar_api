package system.stellar_stay.modules.identify.service;

import system.stellar_stay.modules.identify.dto.permissions.PermissionGroupResponseDTO;
import system.stellar_stay.modules.identify.entity.Permission;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionService {
    public Set<String> getPermissionsForAccount(UUID accountId);
    public Set<String> getPermissionsByRolesId(Set<UUID> roleId);
    public void invalidatePermissions(UUID accountId);
    Set<PermissionGroupResponseDTO> getAllPermissions();
    Set<Permission> getAllByPermissionIds(Set<UUID> permissionIds);
}
