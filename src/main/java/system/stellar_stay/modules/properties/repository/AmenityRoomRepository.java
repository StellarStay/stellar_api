package system.stellar_stay.modules.properties.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import system.stellar_stay.modules.properties.entity.RoomAmenitiesEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AmenityRoomRepository extends JpaRepository<RoomAmenitiesEntity, RoomAmenitiesEntity.RoomAmenitiesId> {

    void deleteByRoomId(UUID roomId);


    @Modifying
    @Query("""
        delete from RoomAmenitiesEntity ra
        where ra.amenities.id in :amenitiesId
        """)
    void deleteByAmenitiesId(List<UUID> amenitiesId);
}
