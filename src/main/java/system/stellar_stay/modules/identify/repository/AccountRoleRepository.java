package system.stellar_stay.modules.identify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import system.stellar_stay.modules.identify.entity.AccountRole;

import java.util.Set;
import java.util.UUID;

public interface AccountRoleRepository extends JpaRepository<AccountRole,AccountRole.AccountRoleId> {

    @Query("""
        select distinct p.name
        from AccountRole ar
        join Role r on r.id = ar.role.id
        join RolePermission rp on rp.role.id = r.id
        join Permission p on p.id = rp.permission.id
        where ar.account.id = :accountId
    """)
    Set<String> findAllPermissionsByAccountId(@Param("accountId") UUID accountId);
}
