package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.repository.AccountRoleRepository;
import system.stellar_stay.modules.identify.service.AccountRoleService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;

import javax.naming.NoPermissionException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountRoleServiceImpl implements AccountRoleService {

    private final AccountRoleRepository accountRoleRepository;

    @Override
    public Set<String> getPermissionsByAccountId(UUID accountId) {
        Set<String> permissions = new HashSet<>();
        permissions = accountRoleRepository.findAllPermissionsByAccountId(accountId);
        if(permissions.isEmpty()) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "No permissions found");
        }
        return permissions;
    }
}
