package system.stellar_stay.modules.properties.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.ListRoomResponseDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForCustomerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseForManagerDTO;
import system.stellar_stay.modules.properties.entity.PropertiesEntity;
import system.stellar_stay.modules.properties.entity.RoomImageEntity;
import system.stellar_stay.modules.properties.entity.RoomsEntity;
import system.stellar_stay.modules.properties.mapper.RoomMapper;
import system.stellar_stay.modules.properties.repository.PropertyRepository;
import system.stellar_stay.modules.properties.repository.RoomRepository;
import system.stellar_stay.modules.properties.service.RoomService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    // FOR MANAGER

    @Override
    public RoomResponseForManagerDTO createRoomForManager(UUID propertyId, RoomCreateRequestForManagerDTO request) {
        if (propertyId == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "PropertyID is not null");
        }
        PropertiesEntity foundProperty = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found"));

        RoomsEntity createdRoom = roomMapper.toCreateRoomEntityForManager(request);
        createdRoom.setProperty(foundProperty);
        createdRoom.setAvailable(true); // Mặc định khi tạo mới phòng sẽ có trạng thái là available, sau này manager có thể update lại nếu muốn
        roomRepository.save(createdRoom);

        return roomMapper.toRoomResponseForManager(createdRoom);
    }

    @Override
    public RoomResponseForManagerDTO updateRoomForManager(UUID roomId, RoomUpdateRequestForManagerDTO request) {
        if(roomId == null){
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "RoomID is not null");
        }
        // Flow khi update: Manager bấm vào properties detail -> list room -> ấn vô room --> Lúc này sẽ không bao giờ có chuyện update room của property này thành property khác được
        // --> Không cần check room có thuộc property nào hay không nữa
        RoomsEntity foundRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Room not found"));

        roomMapper.toUpdateRoomEntityForManager(request, foundRoom);
        roomRepository.save(foundRoom);

        return roomMapper.toRoomResponseForManager(foundRoom);
    }

    @Override
    public RoomResponseDetailForManagerDTO getDetailRoomForManager(UUID roomId) {
        if (roomId == null){
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "RoomID is not null");
        }
        RoomsEntity foundRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Room not found"));


        RoomResponseDetailForManagerDTO roomDetailResponse = roomMapper.toRoomDetailResponseForManager(foundRoom);
        List<RoomImageEntity> listRoomImages = foundRoom.getImages();
        roomDetailResponse.setRoomImages(roomMapper.toListRoomImageResponseDTO(listRoomImages));
        roomDetailResponse.setRoomAmenities(null);
        return roomDetailResponse;
    }

    @Override
    public Page<ListRoomResponseDTO> getAllRoomsForManager(int page, int size, String sortBy, String sortDir, String keyword, UUID propertyId) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RoomsEntity> roomsPage = roomRepository.getAllRoomForManager(propertyId, keyword, pageable);

        // convert từ entity sang DTO
        return roomsPage.map(
                room ->{
                    ListRoomResponseDTO roomResponse = roomMapper.toListRoomDTO(room);
                    room.getImages().stream()
                            .filter(RoomImageEntity::isPrimary)
                            .findFirst()
                            .ifPresent(thumbnailImage -> roomResponse.setUrlImageThumbnail(thumbnailImage.getUrl()));
                    return roomResponse;
                }
        );
        // Nhìn qua mapper, nếu ở service dùng map toListRoomDTO ấy, thì lúc này công việc của thằng map sẽ là map TỪNG ENTITY sang DTO,
        // Rồi sau khi map xong thì nó mới đóng gói thành Page để response thôi

//        return roomMapper.toListRoomDTOPage(roomsPage);
        // Còn nếu dùng thằng này thì hàm toListRoomDTOPage sẽ nhận vào 1 Page<RoomsEntity> rồi trong hàm này,
        // nó sẽ map từng entity sang DTO rồi mới đóng gói thành Page<ListRoomResponseDTO> để trả về
        // Bởi vì nó sài ké thằng toListRoomDTO ở trên để map từng entity sang DTO nên code sẽ gọn hơn,
        // còn nếu dùng thằng map ở trên thì code sẽ dài hơn vì phải viết thêm 1 dòng để map từng entity sang DTO rồi mới đóng gói thành Page để trả về
        // Bản chất là vị trí xử lý cho việc map này thì phải ở trên layer mapper chứ k phải đem qua service để map nữa nên dùng cách này sẽ gọn hơn
    }




    // FOR PUBLIC AND CUSTOMER

    @Override
    public RoomResponseDetailForCustomerDTO getDetailRoomForCustomer(UUID roomId) {
        if (roomId == null){
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "RoomID is not null");
        }
        RoomsEntity foundRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Room not found"));

        RoomResponseDetailForCustomerDTO roomDetailResponse = roomMapper.toRoomDetailResponseForCustomer(foundRoom);
        List<RoomImageEntity> listRoomImages = foundRoom.getImages();
        roomDetailResponse.setRoomImages(roomMapper.toListRoomImageResponseDTO(listRoomImages));
        roomDetailResponse.setRoomAmenities(null);
        return roomDetailResponse;
    }

    @Override
    public Page<ListRoomResponseDTO> getAllRoomsForPublic(int page, int size, String sortBy, String sortDir, String keyword, UUID propertyId) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RoomsEntity> roomsPage = roomRepository.getAllRoomForPublic(propertyId, keyword, pageable);

        return roomsPage.map(
                room ->{
                    ListRoomResponseDTO roomResponse = roomMapper.toListRoomDTO(room);
                    room.getImages().stream()
                            .filter(RoomImageEntity::isPrimary)
                            .findFirst()
                            .ifPresent(thumbnailImage -> roomResponse.setUrlImageThumbnail(thumbnailImage.getUrl()));
                    return roomResponse;
                }
        );
    }
}
