package system.stellar_stay.modules.properties.dto.room_amenities.request;

import java.util.List;
import java.util.UUID;

public record AssignAmenitiesForRoomRequestDTO(
        List<UUID> amenitiesIdList
) {
}
