package system.stellar_stay.modules.identify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import system.stellar_stay.modules.identify.entity.Role;

import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Role findByCode(String code);


    @Query("""
        select r.code
        from Role r
        join AccountRole ar on r.id = ar.role.id
        where ar.account.id = :accountId
    """)
    Set<String> findRolesByAccountId(UUID accountId);

    @Query("""
        select r
        from Role r
        join AccountRole ar on r.id = ar.role.id
        where ar.account.id = :accountId
    """)
    Set<Role> findRolesByAccountIdForAdmin(UUID accountId);

    @Query("""
        select a.id
        from Account a
        join AccountRole ar on a.id = ar.account.id
        where ar.role.id = :roleId
    """)
    Set<UUID> findAllAccountIdsByRoleId(UUID roleId);

    @Query("""
        select r
        from Role r
        where r.id in :roleIds
        """)
    Set<Role> findByIdIn(Set<UUID> roleIds);
}
