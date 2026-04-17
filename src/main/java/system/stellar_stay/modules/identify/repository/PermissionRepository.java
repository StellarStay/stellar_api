package system.stellar_stay.modules.identify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import system.stellar_stay.modules.identify.entity.Permission;

import java.util.Set;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {


    @Query("""
            select distinct p.name
            from Permission p
            join RolePermission rp on p.id = rp.permission.id
            join AccountRole ar on rp.role.id = ar.role.id
            where ar.account.id = :accountId
            """)
    Set<String> findPermissionByAccountId(UUID accountId);

    @Query("""
            select distinct p.name
            from Permission p
            join RolePermission rp on p.id = rp.permission.id
            where rp.role.id in :roleIds
            """)
    Set<String> findPermissionByRoleIds(Set<UUID> roleIds);


    @Query("""
            select p
            from Permission p
            where p.id in :permissionIds
            """)
    Set<Permission> findAllByPermissionIds(Set<UUID> permissionIds);


    @Query("""
            select p
            from Permission p
            """)
    Set<Permission> findAllPermissions();
}
