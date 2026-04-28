package system.stellar_stay.modules.properties.dto.room_images.request;

import java.util.List;

public record RoomImagesRequestDTO(
        List<RoomImageItemRequestDTO> listImages
) {
}
