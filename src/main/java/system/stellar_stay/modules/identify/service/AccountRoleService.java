package system.stellar_stay.modules.identify.service;

import java.util.Set;
import java.util.UUID;

public interface AccountRoleService {
        Set<String> getPermissionsByAccountId(UUID accountId);
}
