package system.stellar_stay.modules.identify.service;

import java.util.Set;
import java.util.UUID;

public interface PermissionService {
    public Set<String> getPermissionsForAccount(UUID accountId);
    public void invalidatePermissions(UUID accountId);
}
