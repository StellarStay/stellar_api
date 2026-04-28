package system.stellar_stay.modules.properties.dto.rooms.response;

import system.stellar_stay.modules.properties.enums.RoomType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ListRoomResponseDTO(
        UUID roomId,
        String name,
        String roomNumber,
        RoomType roomType,
        String description,
        int maxOccupancy,
        BigDecimal area,
        BigDecimal basePrice,
        String currency,
        String urlImageThumbnail
) {
}
