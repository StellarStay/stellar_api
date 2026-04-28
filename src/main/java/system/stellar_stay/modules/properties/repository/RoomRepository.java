package system.stellar_stay.modules.properties.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import system.stellar_stay.modules.properties.entity.RoomsEntity;

import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<RoomsEntity, UUID> {

}
