package system.stellar_stay.modules.properties.dto.rooms.response;

import system.stellar_stay.modules.properties.dto.amenities.response.AmenityResponseDTO;
import system.stellar_stay.modules.properties.dto.room_images.response.RoomImageResponseDTO;
import system.stellar_stay.modules.properties.enums.RoomType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record RoomResponseDetailForCustomerDTO(
        UUID roomId,
        String name,
        RoomType roomType,
        String description,
        int maxOccupancy,
        int floor,
        BigDecimal area,
        BigDecimal basePrice,
        String currency,
        List<RoomImageResponseDTO> roomImages,
        List<AmenityResponseDTO> roomAmenities
) {
}
