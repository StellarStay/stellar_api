package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.service.AccountRoleService;
import system.stellar_stay.modules.identify.service.PermissionService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.infrastructure.caches.config.RedisConfig;
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
    private final AccountRoleService accountRoleService;

    @Override
    public Set<String> getPermissionsForAccount(UUID accountId) {
//      Idea là đầu tiên hãy check trong redis có các permission theo accountId đó không?
//      Nếu có thì lấy trong redis luôn
//      Nếu không thì fetch xuống DB để lấy permission

        String redisKey = RedisKeys.permissionKey(accountId);

        // 1. Check in Redis
        Set<Object> cachesPermissions = redisSupported.getSet(redisKey);
        if (cachesPermissions != null) {
            return cachesPermissions.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }

        // 2. Fetch from DB
        Set<String> permissions = getPermissionByAccountId(accountId);
        if (permissions.isEmpty()) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "No permissions found");
        }

//      Cached to redis after fetching from DB
        redisSupported.setSet(redisKey, permissions, RedisKeys.TTL_PERMISSION);
        return permissions;

    }

    @Override
    public void invalidatePermissions(UUID accountId) {
//        Đây là hàm sẽ được gọi khi có sự thay đổi về role hoặc permission của account đó, để đảm bảo cache luôn mới nhất
//        Thì idea sẽ là tìm các permission liên quan đến accountId đó trong redis và xóa chúng đi
        String keyRedis = RedisKeys.permissionKey(accountId);
        redisSupported.delete(keyRedis);
    }

    private Set<String> getPermissionByAccountId(UUID accountId){
        if (accountId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Account ID cannot be null");
        }
        return accountRoleService.getPermissionsByAccountId(accountId);
    }
}
