package system.stellar_stay.modules.properties.dto.room_images.response;

import system.stellar_stay.modules.properties.enums.MediaType;

public record RoomImageResponseDTO(
    String url,
    MediaType type,
    int sortOrder,
    boolean isPrimary
) {
}
