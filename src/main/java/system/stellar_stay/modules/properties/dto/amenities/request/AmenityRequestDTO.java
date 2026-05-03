package system.stellar_stay.modules.properties.dto.amenities.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import system.stellar_stay.modules.properties.enums.AmenitiesType;

public record AmenityRequestDTO(
        @NotNull(message = "Icon name cannot be null")
        @NotBlank(message = "Icon name cannot be blank")
        String iconName,
        @NotNull(message = "Description cannot be null")
        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotNull(message = "Amenities type cannot be blank")
        @NotBlank(message = "Amenities type cannot be blank")
        AmenitiesType amenitiesType
) {
}
