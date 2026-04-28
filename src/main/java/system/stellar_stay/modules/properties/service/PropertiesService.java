package system.stellar_stay.modules.properties.service;

import org.springframework.data.domain.Page;
import system.stellar_stay.modules.properties.dto.properties.request.PropertiesUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.request.PropertyCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.response.ListPropertiesResponseForAdminDTO;
import system.stellar_stay.modules.properties.dto.properties.response.ListPropertiesResponseForManagerDTO;
import system.stellar_stay.modules.properties.enums.PropertiesStatus;

import java.util.Set;
import java.util.UUID;

public interface PropertiesService {

    // Function for manager
    ListPropertiesResponseForManagerDTO requestCreatePropertiesForManager(PropertyCreateRequestForManagerDTO propertyCreateRequestForManagerDTO);
    ListPropertiesResponseForManagerDTO updatePropertiesForManager(UUID propertyId, PropertiesUpdateRequestForManagerDTO propertyUpdateRequestForManagerDTO);
    Set<ListPropertiesResponseForManagerDTO> getPropertiesByManagerId();
    // Để phần detail thì làm sau, tại nó dính tới room nữa
//    PropertyResponseForManagerDTO getDetailPropertiesByIdForManager(UUID propertyId);



    // Function for admin
    void updatedStatusPropertiesForRequest(UUID propertyId, PropertiesStatus status, String reason);
    void updatePropertiesStatusForAdmin(UUID propertyId, PropertiesStatus status);
    Page<ListPropertiesResponseForAdminDTO> getAllPropertiesForAdmin(int page, int size, String sortBy, String sortDir, String keyword);
    // Để phần detail thì làm sau, tại nó dính tới room nữa, khi admin xem detail property thì phải show full room ra luôn
    //    PropertyResponseForAdminDTO getDetailPropertiesByIdForAdmin(UUID propertyId);

}
