package system.stellar_stay.modules.identify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import system.stellar_stay.modules.identify.entity.RolePermission;

import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission,RolePermission.RolePermissionId> {

    @Modifying
    @Transactional
    @Query("""
        delete
        from RolePermission rp
        where rp.role.id = :roleId
    """)
    void deleteByRoleId(UUID roleId);
}
