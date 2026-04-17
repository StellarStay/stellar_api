package system.stellar_stay.modules.identify.mapper;

import org.mapstruct.*;
import system.stellar_stay.modules.identify.dto.roles.request.RoleRequestDTO;
import system.stellar_stay.modules.identify.dto.roles.response.RoleInformationResponseDTO;
import system.stellar_stay.modules.identify.dto.roles.response.RoleResponseDTO;
import system.stellar_stay.modules.identify.entity.Role;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "code", source = "role.code")
    @Mapping(target = "name", source = "role.name")
    @Mapping(target = "description", source = "role.description")
    // permission thì để map thủ công trong service
    @Mapping(target = "permissions", ignore = true)

    RoleResponseDTO toRoleResponse(Role role);

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "code", source = "role.code")
    @Mapping(target = "name", source = "role.name")
    @Mapping(target = "description", source = "role.description")
    RoleInformationResponseDTO toRoleInformationResponse(Role role);

    @Mapping(target = "id", ignore = true) // id sẽ do database tự sinh ra nên không cần map từ DTO
    @Mapping(target = "code", source = "roleRequestDTO.code")
    @Mapping(target = "name", source = "roleRequestDTO.name")
    @Mapping(target = "description", source = "roleRequestDTO.description")
    Role toRoleEntity(RoleRequestDTO roleRequestDTO);

    @Mapping(target = "id", ignore = true)
    void updateRoleFromDTO(RoleRequestDTO roleRequestDTO, @MappingTarget Role role);
}
