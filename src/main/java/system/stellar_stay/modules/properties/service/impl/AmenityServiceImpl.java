package system.stellar_stay.modules.properties.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.properties.dto.amenities.request.AmenityForUpdateRequestDTO;
import system.stellar_stay.modules.properties.dto.amenities.request.AmenityRequestDTO;
import system.stellar_stay.modules.properties.dto.amenities.response.AmenityItemResponseDTO;
import system.stellar_stay.modules.properties.dto.room_amenities.request.AssignAmenitiesForRoomRequestDTO;
import system.stellar_stay.modules.properties.entity.AmenitiesEntity;
import system.stellar_stay.modules.properties.entity.RoomAmenitiesEntity;
import system.stellar_stay.modules.properties.entity.RoomsEntity;
import system.stellar_stay.modules.properties.mapper.AmenityMapper;
import system.stellar_stay.modules.properties.repository.AmenityRepository;
import system.stellar_stay.modules.properties.repository.AmenityRoomRepository;
import system.stellar_stay.modules.properties.repository.RoomRepository;
import system.stellar_stay.modules.properties.service.AmenityServices;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityServices{

    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;
    private final AmenityRoomRepository amenityRoomRepository;
    private final RoomRepository roomRepository;



    // For Admin
    @Override
    public AmenityItemResponseDTO createAmenity(AmenityRequestDTO requestDTO) {

        AmenitiesEntity amenitiesEntity = amenityMapper.toCreateAmenityEntity(requestDTO);
        AmenitiesEntity savedAmenity = amenityRepository.save(amenitiesEntity);
        return amenityMapper.toResponseAmenityItemDTO(savedAmenity);
    }


    @Override
    public AmenityItemResponseDTO updateInformationOfAmenity(UUID amenityId, AmenityForUpdateRequestDTO requestUpdateDTO) {
        if (amenityId == null){
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "AmenityId is not null");
        }
        AmenitiesEntity foundAmenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Amenity not found"));

        amenityMapper.updateAmenityEntity(requestUpdateDTO, foundAmenity);
        AmenitiesEntity updatedAmenity = amenityRepository.save(foundAmenity);
        return amenityMapper.toResponseAmenityItemDTO(updatedAmenity);
    }


    @Override
    public void deleteAmenity(List<UUID> amenitiesId) {
        if (amenitiesId == null){
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "AmenityId is not null");
        }
        amenityRoomRepository.deleteByAmenitiesId(amenitiesId);
        amenityRepository.deleteByAmenitiesIds(amenitiesId);
    }


    @Override
    public Page<AmenityItemResponseDTO> getAllAmenities(String keyword, int page, int size, String sortDir, String sortBy) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();


        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AmenitiesEntity> pageAmenities = amenityRepository.getAllAmenities(keyword, pageable);
        return pageAmenities.map(amenityMapper::toResponseAmenityItemDTO);
    }


    // For Manager
    @Override
    public Page<AmenityItemResponseDTO> getAllAmenitiesByRoomId(UUID roomId, String keyword, int page, int size, String sortDir, String sortBy) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();


        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AmenitiesEntity> pageAmenitiesForRoom = amenityRepository.getAllAmenitiesByRoomId(keyword, roomId, pageable);
        return pageAmenitiesForRoom.map(amenityMapper::toResponseAmenityItemDTO);
    }


    @Override
    public void assignAmenitiesForRooms(UUID roomId, AssignAmenitiesForRoomRequestDTO requestDTO) {
        // Đầu tiên từ roomId, tìm ra các amenities thuộc trong room đó
        // xóa các amenities đó đi -> rồi sau đó mới insert lại
        RoomsEntity roomFound = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Room not found"));

        // Xóa các amenities đó đi
        amenityRoomRepository.deleteByRoomId(roomId);
        // Lấy ra listAmenitiesId từ list của request
        List<UUID> amenitiesIdList = requestDTO.amenitiesIdList();
        // Từ list đó, lấy ra từng object theo từng id đó, bởi vì trong entity nó lưu đối tượng chứ không phải lưu theo Id đâu
        List<AmenitiesEntity> amenitiesEntityList = amenityRepository.getAllAmenitiesByIds(amenitiesIdList);
        if(amenitiesEntityList == null || amenitiesEntityList.isEmpty()){
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Amenities not found");
        }
        List<RoomAmenitiesEntity> roomAmenitiesEntities = amenitiesEntityList.stream()
                .map(amenitiesEntity -> new RoomAmenitiesEntity(roomFound, amenitiesEntity))
                .toList();

        amenityRoomRepository.saveAll(roomAmenitiesEntities);

    }
}
