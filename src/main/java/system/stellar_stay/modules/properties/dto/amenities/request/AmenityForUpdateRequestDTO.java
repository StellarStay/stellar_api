package system.stellar_stay.modules.properties.dto.amenities.request;

import system.stellar_stay.modules.properties.enums.AmenitiesType;

public record AmenityForUpdateRequestDTO(
        String iconName,
        String description,
        AmenitiesType amenitiesType
) {
}
