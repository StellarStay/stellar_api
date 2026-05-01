package system.stellar_stay.modules.properties.dto.property_images.request;

import java.util.List;

public record PropertyImagesRequestDTO(
        List<PropertyImageItemRequestDTO> listPropertyImages
) {
}
