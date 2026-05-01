package system.stellar_stay.modules.properties.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import system.stellar_stay.modules.properties.entity.PropertiesEntity;

import java.util.Set;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<PropertiesEntity, UUID> {

    @Query(value = """
        SELECT p
        FROM PropertiesEntity p
        LEFT JOIN FETCH p.account a
        LEFT JOIN FETCH a.profile pr
        WHERE p.account.id = :managerId AND (:keyword IS NULL
            OR LOWER(p.address) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.city) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.district) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.ward) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(a.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(pr.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        """,
            countQuery = """
        SELECT COUNT(p)
        FROM PropertiesEntity p
        LEFT JOIN p.account a
        LEFT JOIN a.profile pr
        WHERE p.account.id = :managerId AND (:keyword IS NULL
            OR LOWER(p.address) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.city) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.district) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.ward) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(a.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(pr.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        """)
    Page<PropertiesEntity> findPropertyByManagerId(@Param("managerId") UUID managerId , @Param("keyword") String keyword, Pageable pageable);


    @Query(value = """
        SELECT p
        FROM PropertiesEntity p
        LEFT JOIN FETCH p.account a
        LEFT JOIN FETCH a.profile pr
        WHERE (:keyword IS NULL
            OR LOWER(p.address) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.city) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.district) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.ward) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(a.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(pr.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        """,
            countQuery = """
        SELECT COUNT(p)
        FROM PropertiesEntity p
        LEFT JOIN p.account a
        LEFT JOIN a.profile pr
        WHERE (:keyword IS NULL
            OR LOWER(p.address) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.city) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.district) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.ward) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(a.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(pr.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        """)
    Page<PropertiesEntity> getAllProperties(@Param("keyword") String keyword, Pageable pageable);
}
