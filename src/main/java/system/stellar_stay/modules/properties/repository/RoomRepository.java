package system.stellar_stay.modules.properties.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import system.stellar_stay.modules.properties.entity.RoomsEntity;

import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<RoomsEntity, UUID> {


    @Query(value = """
        SELECT r
        FROM RoomsEntity r
        WHERE r.property.id = :propertyId AND (:keyword IS NULL
            OR LOWER(r.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(r.roomType) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        """,
            countQuery = """
        SELECT COUNT(DISTINCT r)
        FROM RoomsEntity r
        WHERE r.property.id = :propertyId AND (:keyword IS NULL
            OR LOWER(r.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(r.roomType) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        """)
    Page<RoomsEntity> getAllRoomForManager(@Param("propertyId") UUID propertyId,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);


    @Query(value = """
        SELECT r
        FROM RoomsEntity r
        WHERE r.property.id = :propertyId AND (:keyword IS NULL
            OR LOWER(r.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(r.roomType) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
            AND r.isAvailable = true
        """,
            countQuery = """
        SELECT COUNT(DISTINCT r)
        FROM RoomsEntity r
        WHERE r.property.id = :propertyId AND (:keyword IS NULL
            OR LOWER(r.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(r.roomType) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
            AND r.isAvailable = true
        """)
    Page<RoomsEntity> getAllRoomForPublic(@Param("propertyId") UUID propertyId,
                                          @Param("keyword") String keyword,
                                          Pageable pageable);
}
