package system.stellar_stay.modules.properties.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import system.stellar_stay.modules.properties.entity.AmenitiesEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface AmenityRepository extends JpaRepository<AmenitiesEntity, UUID> {


    @Query("""
        select a
        from AmenitiesEntity a
        join RoomAmenitiesEntity  ra on a.id = ra.amenities.id
        join RoomsEntity  r on r.id = ra.room.id
        where r.id = :roomId
        """)
    List<AmenitiesEntity> getAmenitiesByRoomId(UUID roomId);


    @Query("""
        select a
        from AmenitiesEntity a
        where a.id in :amenitiesIds
        """)
    List<AmenitiesEntity> getAllAmenitiesByIds(List<UUID> amenitiesIds);


    @Query(value = """
        SELECT a
        FROM AmenitiesEntity a
        WHERE(:keyword IS NULL
            OR LOWER(a.amenitiesType) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(a.iconName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        """,
            countQuery = """
        SELECT COUNT(a)
        FROM AmenitiesEntity a
        WHERE (:keyword IS NULL
            OR LOWER(a.amenitiesType) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(a.iconName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        """)
    Page<AmenitiesEntity> getAllAmenities(@Param("keyword") String keyword,
                                          Pageable pageable);



    @Query(value = """
        SELECT a
        FROM RoomAmenitiesEntity ra
        LEFT JOIN ra.room r
        LEFT JOIN ra.amenities a
        WHERE r.id = :roomId AND (:keyword IS NULL
            OR LOWER(a.amenitiesType) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(a.iconName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        """,
            countQuery = """
        SELECT COUNT(a)
        FROM RoomAmenitiesEntity ra
        LEFT JOIN ra.room r
        LEFT JOIN ra.amenities a
        WHERE r.id = :roomId AND (:keyword IS NULL
            OR LOWER(a.amenitiesType) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(a.iconName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        """)
    Page<AmenitiesEntity> getAllAmenitiesByRoomId(@Param("keyword") String keyword,
                                                  @Param("roomId") UUID roomId,
                                                  Pageable pageable);




    @Modifying
    @Query("""
        delete from AmenitiesEntity a
        where a.id in :amenitiesIds
        """)
    void deleteByAmenitiesIds(List<UUID> amenitiesIds);
}
