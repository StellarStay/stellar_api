package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.dto.roles.request.RoleRequestDTO;
import system.stellar_stay.modules.identify.dto.roles.response.RoleInformationResponseDTO;
import system.stellar_stay.modules.identify.dto.roles.response.RoleResponseDTO;
import system.stellar_stay.modules.identify.entity.Permission;
import system.stellar_stay.modules.identify.entity.Role;
import system.stellar_stay.modules.identify.entity.RolePermission;
import system.stellar_stay.modules.identify.mapper.RoleMapper;
import system.stellar_stay.modules.identify.repository.RolePermissionRepository;
import system.stellar_stay.modules.identify.repository.RoleRepository;
import system.stellar_stay.modules.identify.service.PermissionService;
import system.stellar_stay.modules.identify.service.RefreshTokenService;
import system.stellar_stay.modules.identify.service.RoleService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.infrastructure.caches.helpers.RedisSupported;
import system.stellar_stay.shared.infrastructure.caches.keys.RedisKeys;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import system.stellar_stay.modules.identify.dto.permissions.PermissionGroupResponseDTO;
import system.stellar_stay.modules.identify.dto.permissions.PermissionResponseDTO;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RedisSupported redisSupported;
    private final RoleMapper roleMapper;
    private final PermissionService permissionService;
    private final RolePermissionRepository rolePermissionRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    public RoleResponseDTO insertRole(RoleRequestDTO roleRequestDTO) {

        Role roleInserted = roleMapper.toRoleEntity(roleRequestDTO);
        roleRepository.save(roleInserted);

        Set<PermissionGroupResponseDTO> permissionGroupResponses = Collections.emptySet(); // Khởi tạo set rỗng cho permission group responses

        // assign permisison cho role này
        if (roleRequestDTO.permissionIds() != null && !roleRequestDTO.permissionIds().isEmpty()) {
            Set<Permission> permissions = permissionService.getAllByPermissionIds(roleRequestDTO.permissionIds());

            if (permissions == null || permissions.isEmpty()) {
                throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "No permissions found to assign to role");
            }

            Set<RolePermission> rolePermissions = permissions.stream()
                    .map(permission -> new RolePermission(roleInserted, permission))
                    .collect(Collectors.toSet());

            rolePermissionRepository.saveAll(rolePermissions);

            permissionGroupResponses = groupPermissionsByGroupName(permissions);

            return buildRoleResponseDTO(roleInserted, permissionGroupResponses);
        }
        return null;
    }

        @Override
        public RoleResponseDTO updateRole (UUID roleId, RoleRequestDTO roleRequestDTO){
            // Khi cập nhật lại role, yêu cầu phải logout hết tất cả những account đang có role đó
            // tức là sau khi cập nhật role xong thì sẽ gọi hàm invalidatePermissions của PermissionServiceImpl để
            // xóa cache permission của những account đó đi, bắt buộc họ phải login lại để lấy cache permission mới nhất

            if (roleId == null) {
                throw new ApiException(ErrorCode.VALIDATION_ERROR, "Role ID cannot be null");
            }
            // 1. Cập nhật thông tin role
            Role existingRole = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found"));

            roleMapper.updateRoleFromDTO(roleRequestDTO, existingRole);
            roleRepository.save(existingRole);

            Set<PermissionGroupResponseDTO> grouped = Collections.emptySet();

            // 2. Cập nhật lại permission cho role này
            if (roleRequestDTO.permissionIds() != null) {
                Set<Permission> permissions = permissionService.getAllByPermissionIds(roleRequestDTO.permissionIds());

                if (permissions == null || permissions.isEmpty()) {
                    throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "No permissions found to assign to role");
                }

                // Xóa hết tất cả các rolePermission hiện có của role này đi
                rolePermissionRepository.deleteByRoleId(roleId);

                // Sau đó insert lại tất cả các rolePermission mới với permission mới
                Set<RolePermission> rolePermissions = permissions.stream()
                        .map(permission -> new RolePermission(existingRole, permission))
                        .collect(Collectors.toSet());
                rolePermissionRepository.saveAll(rolePermissions);

                grouped = groupPermissionsByGroupName(permissions);

                // Sau khi insert thì xóa cached trong redis đi của tất cả các account đang có role này, để bắt buộc họ phải login lại để lấy cache permission mới nhất
                Set<UUID> accountIds = roleRepository.findAllAccountIdsByRoleId(roleId);

                // Sau đó thì bắt logout tất cả những account đó đi, để đảm bảo họ phải login lại để lấy cache permission mới nhất
                // Có nghĩa là forced logout ở tất cả các account đó đi
                accountIds.forEach(id -> {
                    permissionService.invalidatePermissions(id);
                    refreshTokenService.revokeAllRefreshToken(id);
                });
                return buildRoleResponseDTO(existingRole, grouped);
            }
            return buildRoleResponseDTO(existingRole, grouped);
        }

        @Override
        public void deleteRole (UUID roleId){
            if (roleId == null) {
                throw new ApiException(ErrorCode.VALIDATION_ERROR, "Role ID cannot be null");
            }
            Role existingRole = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found"));

            // Trước khi xóa role thì sẽ xóa hết tất cả các rolePermission liên quan đến role này đi
            rolePermissionRepository.deleteByRoleId(roleId);

            // Sau đó mới xóa role này đi
            roleRepository.delete(existingRole);

             // Sau khi xóa thì xóa cached trong redis đi của tất cả các account đang có role này, để bắt buộc họ phải login lại để lấy cache permission mới nhất
            Set<UUID> accountIds = roleRepository.findAllAccountIdsByRoleId(roleId);

            // Sau đó thì bắt logout tất cả những account đó đi, để đảm bảo họ phải login lại để lấy cache permission mới nhất
            // Có nghĩa là forced logout ở tất cả các account đó đi
            accountIds.forEach(id -> {
                permissionService.invalidatePermissions(id);
                refreshTokenService.revokeAllRefreshToken(id);
            });
        }

        @Override
        public Role findRoleByCode (String code){
            if (code == null || code.isEmpty()) {
                throw new ApiException(ErrorCode.VALIDATION_ERROR, "Role code cannot be null or empty");
            }
            Role role = roleRepository.findByCode(code);
            if (role == null) {
                throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Role code not found");
            }
            return role;
        }

    @Override
    public Set<Role> findRoleByRoleIds(Set<UUID> roleId) {
        if (roleId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Role ID cannot be null");
        }
        Set<Role> roles = roleRepository.findByIdIn(roleId);
        if (roles.isEmpty()) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "No roles found for the given IDs");
        }
        return roles;
    }

    @Override
        public Set<String> findRolesByAccountId (UUID accountId){
            if (accountId == null) {
                throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID cannot be null");
            }
            // Đầu tiên thì sẽ check trong redis xem có tồn tại role của account đó không
            String redisRoleKey = RedisKeys.rolesKey(accountId);

            Set<String> cachedRoles = redisSupported.getSet(redisRoleKey);
            if (cachedRoles != null) {
                return cachedRoles.stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet());
            }

            // Nếu không có ở redis thì mới fetch xuống DB để lấy
            Set<String> roles = roleRepository.findRolesByAccountId(accountId);
            if (roles.isEmpty()) {
                throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "No roles found for account");
            }
            // Sau đó set ngược lại vào redis để lần sau có thể lấy nhanh hơn
            redisSupported.setSet(redisRoleKey, roles, RedisKeys.TTL_PERMISSION_ROLE);
            return roles;
        }

    @Override
    public Set<RoleInformationResponseDTO> findAllRolesByAccountIdForAdmin(UUID accountId) {
        if (accountId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID cannot be null");
        }
        // Lấy tất cả role của account đó từ DB để trả về cho admin, vì đây là hàm dành riêng cho admin nên sẽ không cần phải lấy từ cache ở redis làm gì cả, vì admin sẽ cần phải xem thông tin role mới nhất của account đó
        Set<Role> roleCodes = roleRepository.findRolesByAccountIdForAdmin(accountId);
        if (roleCodes.isEmpty()) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "No roles found for account");
        }
        return roleCodes.stream()
                .map(role -> new RoleInformationResponseDTO(
                        role.getId(),
                        role.getCode(),
                        role.getName(),
                        role.getDescription()
                ))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Role> findEntityRolesByAccountIdForAdminGetEntity(UUID accountId) {
        if (accountId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID cannot be null");
        }
        // Lấy tất cả role của account đó từ DB để trả về cho admin, vì đây là hàm dành riêng cho admin nên sẽ không cần phải lấy từ cache ở redis làm gì cả, vì admin sẽ cần phải xem thông tin role mới nhất của account đó
        return roleRepository.findRolesByAccountIdForAdmin(accountId);
    }

    @Override
    public Set<UUID> findAllRolesIdsFromSetRole(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Roles cannot be null or empty");
        }
        return roles.stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
    }


    // Đây là hàm helpers cho việc grouping permission theo resource để trả về cho client, tức là các permission có cùng resource sẽ được nhóm lại với nhau
        private Set<PermissionGroupResponseDTO> groupPermissionsByGroupName (Set<Permission> permissions) {
            return permissions.stream()
                    .collect(Collectors.groupingBy(Permission::getResource))
                    // Đây là grouping theo resource, tức là các permission có cùng resource sẽ được nhóm lại với nhau
                    .entrySet().stream()
                    // entrySet sẽ trả về một stream các entry của map,
                    // mỗi entry sẽ có key là resource và value là list các permission có cùng resource đó
                    .map(entry -> new PermissionGroupResponseDTO(
                            entry.getKey(), // với key là resource thì sẽ dùng làm group name
                            entry.getValue().stream() // với value là list các permission có cùng resource đó thì sẽ map thành list các PermissionResponseDTO
                                    .map(permission -> new PermissionResponseDTO(
                                            permission.getId(),
                                            permission.getAction(),
                                            permission.getResource()))
                                    .toList()
                    ))
                    .collect(Collectors.toSet());
        }

        private RoleResponseDTO buildRoleResponseDTO(Role role, Set<PermissionGroupResponseDTO> permissionGroupResponses) {
            return new RoleResponseDTO(
                    role.getId(),
                    role.getCode(),
                    role.getName(),
                    role.getDescription(),
                    permissionGroupResponses
            );
        }

    }
