package system.stellar_stay.modules.properties.service;

import org.springframework.data.domain.Page;
import system.stellar_stay.modules.properties.dto.amenities.request.AmenityForUpdateRequestDTO;
import system.stellar_stay.modules.properties.dto.amenities.request.AmenityRequestDTO;
import system.stellar_stay.modules.properties.dto.amenities.response.AmenityItemResponseDTO;
import system.stellar_stay.modules.properties.dto.room_amenities.request.AssignAmenitiesForRoomRequestDTO;

import java.util.List;
import java.util.UUID;

public interface AmenityServices {

    // For ADMIN
    AmenityItemResponseDTO createAmenity(AmenityRequestDTO requestDTO);
    AmenityItemResponseDTO updateInformationOfAmenity(UUID amenityId, AmenityForUpdateRequestDTO requestUpdateDTO);
    void deleteAmenity(List<UUID> amenitiesId);
    Page<AmenityItemResponseDTO> getAllAmenities(String keyword, int page, int size, String sortDir, String sortBy);

    // For MANAGER
    Page<AmenityItemResponseDTO> getAllAmenitiesByRoomId(UUID roomId, String keyword, int page, int size, String sortDir, String sortBy);
    void assignAmenitiesForRooms(UUID roomId, AssignAmenitiesForRoomRequestDTO requestDTO);
    // Flow assign amenities for rooms là như này:
        // user bấm vào room đó -> bấm assign amenities -> lúc này sẽ lấy được roomId rồi + với hiển thị list amenities
        // Chọn Amenities xong -> bấm assign thì lúc này sẽ lấy được roomId + listAmenitiesId -> sau đó thì assign thôi
        // Dưới BE thì lúc này sẽ nạp vào List RoomId và list AmenitesId để insert vô DB thôi
}
