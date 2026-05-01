package system.stellar_stay.modules.properties.service;

import org.springframework.data.domain.Page;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.ListRoomResponseDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForCustomerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseForManagerDTO;

import java.util.UUID;

public interface RoomService {

    // For MANAGER
    RoomResponseForManagerDTO createRoomForManager(UUID propertyId, RoomCreateRequestForManagerDTO request);
    RoomResponseForManagerDTO updateRoomForManager(UUID roomId, RoomUpdateRequestForManagerDTO request);
    RoomResponseDetailForManagerDTO getDetailRoomForManager(UUID roomId);
    //Chỗ này get all cho manager thì phải show hết chứ ko filter theo is_available như public
    Page<ListRoomResponseDTO> getAllRoomsForManager(int page, int size, String sortBy, String sortDir, String keyword, UUID propertyId);


    // For GUEST and CUSTOMER
    RoomResponseDetailForCustomerDTO getDetailRoomForCustomer(UUID roomId);
    // Chỗ này khác nhau trong query nhé, với điều kiện is_available = true, và có thể thêm điều kiện ngày tháng nếu cần thiết để lọc ra những phòng có sẵn trong khoảng thời gian đó.
    Page<ListRoomResponseDTO> getAllRoomsForPublic(int page, int size, String sortBy, String sortDir, String keyword, UUID propertyId);



}
