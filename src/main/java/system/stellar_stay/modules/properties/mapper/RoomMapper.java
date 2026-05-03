package system.stellar_stay.modules.properties.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import system.stellar_stay.modules.properties.dto.room_images.response.RoomImageResponseDTO;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.ListRoomResponseDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForCustomerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseForManagerDTO;
import system.stellar_stay.modules.properties.entity.RoomImageEntity;
import system.stellar_stay.modules.properties.entity.RoomsEntity;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomMapper {


    @Mapping(target = "url", source = "listRoomImageEntity.url")
    @Mapping(target = "mediaType", source = "listRoomImageEntity.mediaType")
    @Mapping(target = "sortOrder", source = "listRoomImageEntity.sortOrder")
    @Mapping(target = "primary", source = "listRoomImageEntity.primary")
    List<RoomImageResponseDTO> toListRoomImageResponseDTO(List<RoomImageEntity> listRoomImageEntity);

    // FOR MANAGER

    @Mapping(target = "roomId", source = "roomsEntity.id")
    @Mapping(target = "name", source = "roomsEntity.name")
    @Mapping(target = "roomNumber", source = "roomsEntity.roomNumber")
    @Mapping(target = "roomType", source = "roomsEntity.roomType")
    @Mapping(target = "description", source = "roomsEntity.description")
    @Mapping(target = "maxOccupancy", source = "roomsEntity.maxOccupancy")
    @Mapping(target = "floor", source = "roomsEntity.floor")
    @Mapping(target = "area", source = "roomsEntity.area")
    @Mapping(target = "basePrice", source = "roomsEntity.basePrice")
    @Mapping(target = "currency", source = "roomsEntity.currency")
    @Mapping(target = "isAvailable", source = "roomsEntity.available")
    RoomResponseForManagerDTO toRoomResponseForManager(RoomsEntity roomsEntity);

    @Mapping(target = "roomId", source = "roomsEntity.id")
    @Mapping(target = "name", source = "roomsEntity.name")
    @Mapping(target = "roomNumber", source = "roomsEntity.roomNumber")
    @Mapping(target = "roomType", source = "roomsEntity.roomType")
    @Mapping(target = "description", source = "roomsEntity.description")
    @Mapping(target = "maxOccupancy", source = "roomsEntity.maxOccupancy")
    @Mapping(target = "floor", source = "roomsEntity.floor")
    @Mapping(target = "area", source = "roomsEntity.area")
    @Mapping(target = "basePrice", source = "roomsEntity.basePrice")
    @Mapping(target = "currency", source = "roomsEntity.currency")
    @Mapping(target = "isAvailable", source = "roomsEntity.available")
    @Mapping(target = "roomImages", ignore = true)
    @Mapping(target = "roomAmenities", ignore = true)
    RoomResponseDetailForManagerDTO toRoomDetailResponseForManager(RoomsEntity roomsEntity);

    @Mapping(target = "name", source = "roomCreateRequestForManagerDTO.name")
    @Mapping(target = "roomNumber", source = "roomCreateRequestForManagerDTO.roomNumber")
    @Mapping(target = "roomType", source = "roomCreateRequestForManagerDTO.roomType")
    @Mapping(target = "description", source = "roomCreateRequestForManagerDTO.description")
    @Mapping(target = "maxOccupancy", source = "roomCreateRequestForManagerDTO.maxOccupancy")
    @Mapping(target = "floor", source = "roomCreateRequestForManagerDTO.floor")
    @Mapping(target = "area", source = "roomCreateRequestForManagerDTO.area")
    @Mapping(target = "basePrice", source = "roomCreateRequestForManagerDTO.basePrice")
    @Mapping(target = "currency", source = "roomCreateRequestForManagerDTO.currency")
    RoomsEntity toCreateRoomEntityForManager(RoomCreateRequestForManagerDTO roomCreateRequestForManagerDTO);



    @Mapping(target = "name", source = "roomUpdateRequestForManagerDTO.name")
    @Mapping(target = "roomNumber", source = "roomUpdateRequestForManagerDTO.roomNumber")
    @Mapping(target = "roomType", source = "roomUpdateRequestForManagerDTO.roomType")
    @Mapping(target = "description", source = "roomUpdateRequestForManagerDTO.description")
    @Mapping(target = "maxOccupancy", source = "roomUpdateRequestForManagerDTO.maxOccupancy")
    @Mapping(target = "floor", source = "roomUpdateRequestForManagerDTO.floor")
    @Mapping(target = "area", source = "roomUpdateRequestForManagerDTO.area")
    @Mapping(target = "basePrice", source = "roomUpdateRequestForManagerDTO.basePrice")
    @Mapping(target = "currency", source = "roomUpdateRequestForManagerDTO.currency")
    @Mapping(target = "available", source = "roomUpdateRequestForManagerDTO.isAvailable")
    void toUpdateRoomEntityForManager(RoomUpdateRequestForManagerDTO roomUpdateRequestForManagerDTO, @MappingTarget RoomsEntity existingRoom);



    @Mapping(target = "roomId", source = "roomsEntity.id")
    @Mapping(target = "name", source = "roomsEntity.name")
    @Mapping(target = "roomNumber", source = "roomsEntity.roomNumber")
    @Mapping(target = "roomType", source = "roomsEntity.roomType")
    @Mapping(target = "description", source = "roomsEntity.description")
    @Mapping(target = "maxOccupancy", source = "roomsEntity.maxOccupancy")
    @Mapping(target = "area", source = "roomsEntity.area")
    @Mapping(target = "basePrice", source = "roomsEntity.basePrice")
    @Mapping(target = "currency", source = "roomsEntity.currency")
    @Mapping(target = "urlImageThumbnail", ignore = true)
    ListRoomResponseDTO toListRoomDTO(RoomsEntity roomsEntity);

    default Page<ListRoomResponseDTO> toListRoomDTOPage(Page<RoomsEntity> roomsEntityPage) {
        if (roomsEntityPage == null) {
            return null;
        }
        return roomsEntityPage.map(this::toListRoomDTO);
    }


    // FOR CUSTOMER

    @Mapping(target = "roomId", source = "roomsEntity.id")
    @Mapping(target = "name", source = "roomsEntity.name")
    @Mapping(target = "roomNumber", source = "roomsEntity.roomNumber")
    @Mapping(target = "roomType", source = "roomsEntity.roomType")
    @Mapping(target = "description", source = "roomsEntity.description")
    @Mapping(target = "maxOccupancy", source = "roomsEntity.maxOccupancy")
    @Mapping(target = "area", source = "roomsEntity.area")
    @Mapping(target = "basePrice", source = "roomsEntity.basePrice")
    @Mapping(target = "currency", source = "roomsEntity.currency")
    @Mapping(target = "isAvailable", source = "roomsEntity.available")
    @Mapping(target = "roomImages", ignore = true)
    @Mapping(target = "roomAmenities", ignore = true)
    RoomResponseDetailForCustomerDTO toRoomDetailResponseForCustomer(RoomsEntity roomsEntity);

}
