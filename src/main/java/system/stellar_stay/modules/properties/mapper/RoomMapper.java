package system.stellar_stay.modules.properties.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import system.stellar_stay.modules.properties.dto.rooms.request.RoomCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.rooms.response.RoomResponseDetailForManagerDTO;
import system.stellar_stay.modules.properties.entity.RoomsEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomMapper {

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
    @Mapping(target = "roomImages", ignore = true)
    @Mapping(target = "roomAmenities", ignore = true)
    RoomResponseDetailForManagerDTO toRoomDetailResponseForManager(RoomsEntity roomsEntity);

    RoomsEntity toCreateRoomEntity(RoomCreateRequestForManagerDTO roomCreateRequestForManagerDTO);






}
