package system.stellar_stay.modules.properties.dto.amenities.response;

import system.stellar_stay.modules.properties.enums.AmenitiesType;

import java.util.UUID;

public record AmenityItemResponseDTO(
        UUID id,
        String iconName,
        String description,
        AmenitiesType amenitiesType
) {
}
