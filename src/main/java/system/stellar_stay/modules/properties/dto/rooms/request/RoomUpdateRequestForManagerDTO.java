package system.stellar_stay.modules.properties.dto.rooms.request;

import system.stellar_stay.modules.properties.enums.RoomType;

import java.math.BigDecimal;

public record RoomUpdateRequestForManagerDTO(
        String name,
        String roomNumber,
        RoomType roomType,
        String description,
        int maxOccupancy,
        int floor,
        BigDecimal area,
        BigDecimal basePrice,
        String currency,
        boolean isAvailable
) {
}
