package system.stellar_stay.modules.identify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import system.stellar_stay.modules.identify.entity.AccountRole;
import system.stellar_stay.modules.identify.entity.Role;

import java.util.Set;
import java.util.UUID;

public interface AccountRoleRepository extends JpaRepository<AccountRole,AccountRole.AccountRoleId> {

    int countByRoleId(UUID roleId);

    void deleteByRoleId(UUID roleId);

    void deleteAccountRoleByAccountId(UUID accountId);
}