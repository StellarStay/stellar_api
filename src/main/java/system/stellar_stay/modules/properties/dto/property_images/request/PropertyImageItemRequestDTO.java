package system.stellar_stay.modules.properties.dto.property_images.request;

import system.stellar_stay.modules.properties.enums.MediaType;

public record PropertyImageItemRequestDTO(
        String url,
        MediaType type,
        int sortOrder,
        boolean isPrimary
) {
}
