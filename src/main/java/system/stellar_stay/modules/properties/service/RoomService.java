package system.stellar_stay.modules.properties.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.ListRoomResponseDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForCustomerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForManagerDTO;

import java.util.UUID;

public interface RoomService {

    // For MANAGER
    RoomResponseDetailForManagerDTO createRoomForManager(UUID propertyId, RoomCreateRequestForManagerDTO request);
    RoomResponseDetailForManagerDTO updateRoomForManager(UUID roomId, RoomUpdateRequestForManagerDTO request);
    RoomResponseDetailForManagerDTO getDetailRoomForManager(UUID roomId);
    Page<ListRoomResponseDTO> getAllRoomsForManager(String keyword, Pageable pageable);


    // For GUEST and CUSTOMER
    RoomResponseDetailForCustomerDTO getDetailRoomForCustomer(UUID roomId);

    // Chỗ này khác nhau trong query nhé, với điều kiện is_available = true, và có thể thêm điều kiện ngày tháng nếu cần thiết để lọc ra những phòng có sẵn trong khoảng thời gian đó.
    Page<ListRoomResponseDTO> getAllRoomsForPublic(String keyword, Pageable pageable);



}
