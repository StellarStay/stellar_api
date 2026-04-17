package system.stellar_stay.modules.identify.service;

import system.stellar_stay.modules.identify.dto.roles.request.RoleRequestDTO;
import system.stellar_stay.modules.identify.dto.roles.response.RoleInformationResponseDTO;
import system.stellar_stay.modules.identify.dto.roles.response.RoleResponseDTO;
import system.stellar_stay.modules.identify.entity.Role;

import java.util.Set;
import java.util.UUID;

public interface RoleService {

    RoleResponseDTO insertRole(RoleRequestDTO roleRequestDTO);

    RoleResponseDTO updateRole(UUID roleId, RoleRequestDTO roleRequestDTO);

    void deleteRole(UUID roleId);

    Role findRoleByCode(String code);

    Set<Role> findRoleByRoleIds(Set<UUID> roleId);

    Set<String> findRolesByAccountId(UUID accountId);

    Set<RoleInformationResponseDTO> findAllRolesByAccountIdForAdmin(UUID accountId);

    Set<Role> findEntityRolesByAccountIdForAdminGetEntity(UUID accountId);

    Set<UUID> findAllRolesIdsFromSetRole(Set<Role> roles);
}
