package system.stellar_stay.modules.properties.dto.rooms.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import system.stellar_stay.modules.properties.dto.amenities.response.AmenityResponseDTO;
import system.stellar_stay.modules.properties.dto.room_images.response.RoomImageResponseDTO;
import system.stellar_stay.modules.properties.enums.RoomType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponseDetailForManagerDTO{
    private UUID roomId;
    private String name;
    private String roomNumber;
    private RoomType roomType;
    private String description;
    private int maxOccupancy;
    private int floor;
    private BigDecimal area;
    private BigDecimal basePrice;
    private String currency;
    private Boolean isAvailable;
    private List<RoomImageResponseDTO> roomImages;
    private List<AmenityResponseDTO> roomAmenities;
}
