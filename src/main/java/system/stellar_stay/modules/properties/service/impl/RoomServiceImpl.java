package system.stellar_stay.modules.properties.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.ListRoomResponseDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForCustomerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForManagerDTO;
import system.stellar_stay.modules.properties.entity.PropertiesEntity;
import system.stellar_stay.modules.properties.repository.PropertyRepository;
import system.stellar_stay.modules.properties.repository.RoomRepository;
import system.stellar_stay.modules.properties.service.RoomService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;

    @Override
    public RoomResponseDetailForManagerDTO createRoomForManager(UUID propertyId, RoomCreateRequestForManagerDTO request) {
        if (propertyId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "PropertyID is not null");
        }
        PropertiesEntity foundProperty = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found"));

        return null;

    }

    @Override
    public RoomResponseDetailForManagerDTO updateRoomForManager(UUID roomId, RoomUpdateRequestForManagerDTO request) {
        return null;
    }

    @Override
    public RoomResponseDetailForManagerDTO getDetailRoomForManager(UUID roomId) {
        return null;
    }

    @Override
    public Page<ListRoomResponseDTO> getAllRoomsForManager(String keyword, Pageable pageable) {
        return null;
    }

    @Override
    public RoomResponseDetailForCustomerDTO getDetailRoomForCustomer(UUID roomId) {
        return null;
    }

    @Override
    public Page<ListRoomResponseDTO> getAllRoomsForPublic(String keyword, Pageable pageable) {
        return null;
    }
}
