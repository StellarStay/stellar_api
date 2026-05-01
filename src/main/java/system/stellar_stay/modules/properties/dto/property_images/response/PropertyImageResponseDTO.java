package system.stellar_stay.modules.properties.dto.property_images.response;

import system.stellar_stay.modules.properties.enums.MediaType;

public record PropertyImageResponseDTO(
        String url,
        MediaType mediaType,
        int sortOrder,
        Boolean primary
) {
}
