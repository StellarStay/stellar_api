package system.stellar_stay.modules.properties.dto.room_images.request;

import system.stellar_stay.modules.properties.enums.MediaType;

public record RoomImageItemRequestDTO(
        String url,
        MediaType type,
        int sortOrder,
        boolean isPrimary
) {
}
