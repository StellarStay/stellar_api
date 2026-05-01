package system.stellar_stay.modules.properties.dto.rooms.response;

import lombok.*;
import system.stellar_stay.modules.properties.enums.RoomType;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ListRoomResponseDTO {
    private UUID roomId;
    private String name;
    private String roomNumber;
    private RoomType roomType;
    private String description;
    private int maxOccupancy;
    private BigDecimal area;
    private BigDecimal basePrice;
    private String currency;
    private String urlImageThumbnail;
}
