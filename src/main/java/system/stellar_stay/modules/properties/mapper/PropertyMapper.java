package system.stellar_stay.modules.properties.mapper;


import org.mapstruct.*;
import system.stellar_stay.modules.identify.entity.Account;
import system.stellar_stay.modules.properties.dto.properties.request.PropertiesUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.request.PropertyCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.response.*;
import system.stellar_stay.modules.properties.dto.property_images.response.PropertyImageResponseDTO;
import system.stellar_stay.modules.properties.entity.PropertiesEntity;
import system.stellar_stay.modules.properties.entity.PropertyImageEntity;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PropertyMapper {



    @Mapping(target = "url", source = "propertyImageEntity.url")
    @Mapping(target = "mediaType", source = "propertyImageEntity.mediaType")
    @Mapping(target = "sortOrder", source = "propertyImageEntity.sortOrder")
    @Mapping(target = "primary", source = "propertyImageEntity.primary")
    List<PropertyImageResponseDTO> toPropertyImageResponseDTO(List<PropertyImageEntity> propertyImageEntity);

    // MANAGER

    @Mapping(target = "propertiesId", source = "propertiesEntity.id")
    @Mapping(target = "name", source = "propertiesEntity.name")
    @Mapping(target = "slug", source = "propertiesEntity.slug")
    @Mapping(target = "type", source = "propertiesEntity.type")
    @Mapping(target = "description", source = "propertiesEntity.description")
    @Mapping(target = "address", source = "propertiesEntity.address")
    @Mapping(target = "city", source = "propertiesEntity.city")
    @Mapping(target = "district", source = "propertiesEntity.district")
    @Mapping(target = "ward", source = "propertiesEntity.ward")
    @Mapping(target = "latitude", source = "propertiesEntity.latitude")
    @Mapping(target = "longitude", source = "propertiesEntity.longitude")
    @Mapping(target = "phone", source = "propertiesEntity.phone")
    @Mapping(target = "email", source = "propertiesEntity.email")
    @Mapping(target = "status", source = "propertiesEntity.status")
    @Mapping(target = "isAvailable", source = "propertiesEntity.available")
    PropertiesResponseForManagerDTO toPropertyResponseForManagerDTO(PropertiesEntity propertiesEntity);

    @Mapping(target = "propertiesId", source = "propertiesEntity.id")
    @Mapping(target = "name", source = "propertiesEntity.name")
    @Mapping(target = "slug", source = "propertiesEntity.slug")
    @Mapping(target = "type", source = "propertiesEntity.type")
    @Mapping(target = "description", source = "propertiesEntity.description")
    @Mapping(target = "address", source = "propertiesEntity.address")
    @Mapping(target = "city", source = "propertiesEntity.city")
    @Mapping(target = "district", source = "propertiesEntity.district")
    @Mapping(target = "ward", source = "propertiesEntity.ward")
    @Mapping(target = "latitude", source = "propertiesEntity.latitude")
    @Mapping(target = "longitude", source = "propertiesEntity.longitude")
    @Mapping(target = "phone", source = "propertiesEntity.phone")
    @Mapping(target = "email", source = "propertiesEntity.email")
    @Mapping(target = "status", source = "propertiesEntity.status")
    @Mapping(target = "isAvailable", source = "propertiesEntity.available")
    @Mapping(target = "listPropertyImage", ignore = true)
    PropertyDetailResponseForManagerDTO toPropertyResponseDetailForManagerDTO(PropertiesEntity propertiesEntity);


    @Mapping(target = "propertiesId", source = "propertiesEntity.id")
    @Mapping(target = "name", source = "propertiesEntity.name")
    @Mapping(target = "slug", source = "propertiesEntity.slug")
    @Mapping(target = "type", source = "propertiesEntity.type")
    @Mapping(target = "description", source = "propertiesEntity.description")
    @Mapping(target = "address", source = "propertiesEntity.address")
    @Mapping(target = "city", source = "propertiesEntity.city")
    @Mapping(target = "district", source = "propertiesEntity.district")
    @Mapping(target = "ward", source = "propertiesEntity.ward")
    @Mapping(target = "phone", source = "propertiesEntity.phone")
    @Mapping(target = "email", source = "propertiesEntity.email")
    @Mapping(target = "urlThumbnailImage", ignore = true)
    ListPropertiesResponseForManager toPropertyListResponseForManager(PropertiesEntity propertiesEntity);


    @Mapping(target = "name", source = "propertyCreateRequestForManagerDTO.name")
    @Mapping(target = "slug", source = "propertyCreateRequestForManagerDTO.slug")
    @Mapping(target = "type", source = "propertyCreateRequestForManagerDTO.type")
    @Mapping(target = "description", source = "propertyCreateRequestForManagerDTO.description")
    @Mapping(target = "address", source = "propertyCreateRequestForManagerDTO.address")
    @Mapping(target = "city", source = "propertyCreateRequestForManagerDTO.city")
    @Mapping(target = "district", source = "propertyCreateRequestForManagerDTO.district")
    @Mapping(target = "ward", source = "propertyCreateRequestForManagerDTO.ward")
    @Mapping(target = "latitude", source = "propertyCreateRequestForManagerDTO.latitude")
    @Mapping(target = "longitude", source = "propertyCreateRequestForManagerDTO.longitude")
    @Mapping(target = "phone", source = "propertyCreateRequestForManagerDTO.phone")
    @Mapping(target = "email", source = "propertyCreateRequestForManagerDTO.email")
    PropertiesEntity toPropertyEntity(PropertyCreateRequestForManagerDTO propertyCreateRequestForManagerDTO);


    @Mapping(target = "name", source = "propertiesUpdateRequestForManagerDTO.name")
    @Mapping(target = "slug", source = "propertiesUpdateRequestForManagerDTO.slug")
    @Mapping(target = "type", source = "propertiesUpdateRequestForManagerDTO.type")
    @Mapping(target = "description", source = "propertiesUpdateRequestForManagerDTO.description")
    @Mapping(target = "address", source = "propertiesUpdateRequestForManagerDTO.address")
    @Mapping(target = "city", source = "propertiesUpdateRequestForManagerDTO.city")
    @Mapping(target = "district", source = "propertiesUpdateRequestForManagerDTO.district")
    @Mapping(target = "ward", source = "propertiesUpdateRequestForManagerDTO.ward")
    @Mapping(target = "latitude", source = "propertiesUpdateRequestForManagerDTO.latitude")
    @Mapping(target = "longitude", source = "propertiesUpdateRequestForManagerDTO.longitude")
    @Mapping(target = "phone", source = "propertiesUpdateRequestForManagerDTO.phone")
    @Mapping(target = "email", source = "propertiesUpdateRequestForManagerDTO.email")
    @Mapping(target = "available", source = "propertiesUpdateRequestForManagerDTO.isAvailable")
    void updatePropertyByManager(PropertiesUpdateRequestForManagerDTO propertiesUpdateRequestForManagerDTO, @MappingTarget PropertiesEntity existingEntity);





    // ADMIN

    @Mapping(target = "managerId", source = "account.id")
    @Mapping(target = "managerName", source = "account.profile.fullName")
    @Mapping(target = "email", source = "account.email")
    @Mapping(target = "phone", source = "account.profile.phoneNumber")
    ManagerResponse managerResponseFromAccount(Account account);

//    @Mapping(target = "propertiesId", source = "propertiesEntity.id")
//    @Mapping(target = "name", source = "propertiesEntity.name")
//    @Mapping(target = "slug", source = "propertiesEntity.slug")
//    @Mapping(target = "type", source = "propertiesEntity.type")
//    @Mapping(target = "description", source = "propertiesEntity.description")
//    @Mapping(target = "address", source = "propertiesEntity.address")
//    @Mapping(target = "city", source = "propertiesEntity.city")
//    @Mapping(target = "district", source = "propertiesEntity.district")
//    @Mapping(target = "ward", source = "propertiesEntity.ward")
//    @Mapping(target = "latitude", source = "propertiesEntity.latitude")
//    @Mapping(target = "longitude", source = "propertiesEntity.longitude")
//    @Mapping(target = "phone", source = "propertiesEntity.phone")
//    @Mapping(target = "email", source = "propertiesEntity.email")
//    @Mapping(target = "status", source = "propertiesEntity.status")
//    @Mapping(target = "isAvailable", source = "propertiesEntity.available")
//    @Mapping(target = "manager", ignore = true) // manager sẽ được set thủ công trong service sau khi lấy thông tin manager từ propertiesEntity
//    PropertiesResponseForAdminDTO toResponseForAdminDTO(PropertiesEntity propertiesEntity);


    @Mapping(target = "propertiesId", source = "propertiesEntity.id")
    @Mapping(target = "name", source = "propertiesEntity.name")
    @Mapping(target = "slug", source = "propertiesEntity.slug")
    @Mapping(target = "type", source = "propertiesEntity.type")
    @Mapping(target = "description", source = "propertiesEntity.description")
    @Mapping(target = "address", source = "propertiesEntity.address")
    @Mapping(target = "city", source = "propertiesEntity.city")
    @Mapping(target = "district", source = "propertiesEntity.district")
    @Mapping(target = "ward", source = "propertiesEntity.ward")
    @Mapping(target = "phone", source = "propertiesEntity.phone")
    @Mapping(target = "email", source = "propertiesEntity.email")
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "urlThumbnailImage", ignore = true)
    ListPropertiesResponseForAdmin toListResponseForAdmin(PropertiesEntity propertiesEntity);

    @Mapping(target = "propertiesId", source = "propertiesEntity.id")
    @Mapping(target = "name", source = "propertiesEntity.name")
    @Mapping(target = "slug", source = "propertiesEntity.slug")
    @Mapping(target = "type", source = "propertiesEntity.type")
    @Mapping(target = "description", source = "propertiesEntity.description")
    @Mapping(target = "address", source = "propertiesEntity.address")
    @Mapping(target = "city", source = "propertiesEntity.city")
    @Mapping(target = "district", source = "propertiesEntity.district")
    @Mapping(target = "ward", source = "propertiesEntity.ward")
    @Mapping(target = "latitude", source = "propertiesEntity.latitude")
    @Mapping(target = "longitude", source = "propertiesEntity.longitude")
    @Mapping(target = "phone", source = "propertiesEntity.phone")
    @Mapping(target = "email", source = "propertiesEntity.email")
    @Mapping(target = "status", source = "propertiesEntity.status")
    @Mapping(target = "isAvailable", source = "propertiesEntity.available")
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "listPropertyImage", ignore = true)
    PropertyDetailResponseForAdminDTO toPropertyResponseDetailForAdminDTO(PropertiesEntity propertiesEntity);
}
