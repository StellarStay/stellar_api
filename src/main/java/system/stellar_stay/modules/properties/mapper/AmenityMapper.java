package system.stellar_stay.modules.properties.mapper;

import org.mapstruct.*;
import system.stellar_stay.modules.properties.dto.amenities.request.AmenityForUpdateRequestDTO;
import system.stellar_stay.modules.properties.dto.amenities.request.AmenityRequestDTO;
import system.stellar_stay.modules.properties.dto.amenities.response.AmenityItemResponseDTO;
import system.stellar_stay.modules.properties.entity.AmenitiesEntity;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AmenityMapper {

    @Mapping(target = "iconName", source = "amenitiesEntity.iconName")
    @Mapping(target = "description", source = "amenitiesEntity.description")
    @Mapping(target = "amenitiesType", source = "amenitiesEntity.amenitiesType")
    AmenityItemResponseDTO toResponseAmenityItemDTO(AmenitiesEntity amenitiesEntity);


    @Mapping(target = "iconName", source = "requestDTO.iconName")
    @Mapping(target = "description", source = "requestDTO.description")
    @Mapping(target = "amenitiesType", source = "requestDTO.amenitiesType")
    AmenitiesEntity toCreateAmenityEntity(AmenityRequestDTO requestDTO);

    @Mapping(target = "iconName", source = "requestForUpdate.iconName")
    @Mapping(target = "description", source = "requestForUpdate.description")
    @Mapping(target = "amenitiesType", source = "requestForUpdate.amenitiesType")
    void updateAmenityEntity(AmenityForUpdateRequestDTO requestForUpdate, @MappingTarget AmenitiesEntity amenitiesEntity);

    @Mapping(target = "iconName", source = "amenitiesEntities.iconName")
    @Mapping(target = "description", source = "amenitiesEntities.description")
    @Mapping(target = "amenitiesType", source = "amenitiesEntities.amenitiesType")
    List<AmenityItemResponseDTO> toResponseAmenitiesListDTO(List<AmenitiesEntity> amenitiesEntities);
}
