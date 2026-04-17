package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.dto.permissions.PermissionGroupResponseDTO;
import system.stellar_stay.modules.identify.dto.permissions.PermissionResponseDTO;
import system.stellar_stay.modules.identify.entity.Permission;
import system.stellar_stay.modules.identify.repository.PermissionRepository;
import system.stellar_stay.modules.identify.service.PermissionService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.infrastructure.caches.helpers.RedisSupported;
import system.stellar_stay.shared.infrastructure.caches.keys.RedisKeys;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final RedisSupported redisSupported;
    private final PermissionRepository permissionRepository;

    @Override
    public Set<String> getPermissionsForAccount(UUID accountId) {
//      Idea là đầu tiên hãy check trong redis có các permission theo accountId đó không?
//      Nếu có thì lấy trong redis luôn
//      Nếu không thì fetch xuống DB để lấy permission

        String redisKey = RedisKeys.permissionKey(accountId);

        // 1. Check in Redis
            // Create redis key and from key, get some permission
        Set<String> cachesPermissions = redisSupported.getSet(redisKey);
        if (cachesPermissions != null) {
            return cachesPermissions.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }

        // 2. Fetch from DB
        Set<String> permissions = permissionRepository.findPermissionByAccountId(accountId);
        if (permissions.isEmpty()) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "No permissions found");
        }

//      Cached to redis after fetching from DB
        redisSupported.setSet(redisKey, permissions, RedisKeys.TTL_PERMISSION_ROLE);
        return permissions;
    }

    @Override
    public Set<String> getPermissionsByRolesId(Set<UUID> roleId) {
        if (roleId.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Role IDs cannot be empty");
        }
        Set<String> permissions = permissionRepository.findPermissionByRoleIds(roleId);
        if (permissions.isEmpty()) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "No permissions found for the given role IDs");
        }
        return permissions;
    }

    @Override
    public void invalidatePermissions(UUID accountId) {
//        Đây là hàm sẽ được gọi khi có sự thay đổi về role hoặc permission của account đó, để đảm bảo cache luôn mới nhất
//        Thì idea sẽ là tìm các permission liên quan đến accountId đó trong redis và xóa chúng đi
        String permissionKey = RedisKeys.permissionKey(accountId);
        String roleKey = RedisKeys.rolesKey(accountId);
        redisSupported.delete(permissionKey);
        redisSupported.delete(roleKey);
    }

    @Override
    public Set<PermissionGroupResponseDTO> getAllPermissions() {
        Set<Permission> permissions = permissionRepository.findAllPermissions();

        if (permissions.isEmpty()) {
            return new HashSet<>();
        }

        // Group permissions by resource
        return permissions.stream()
                .collect(Collectors.groupingBy(Permission::getResource))
                .entrySet()
                .stream()
                .map(entry -> new PermissionGroupResponseDTO(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(permission -> new PermissionResponseDTO(
                                        permission.getId(),
                                        permission.getAction(),
                                        permission.getDescription()
                                ))
                                .toList()
                ))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Permission> getAllByPermissionIds(Set<UUID> permissionIds) {
        if (permissionIds.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Permission IDs cannot be empty");
        }
        return permissionRepository.findAllByPermissionIds(permissionIds);

    }
}
