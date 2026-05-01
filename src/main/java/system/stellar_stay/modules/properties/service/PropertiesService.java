package system.stellar_stay.modules.properties.service;

import org.springframework.data.domain.Page;
import system.stellar_stay.modules.properties.dto.properties.request.PropertiesUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.request.PropertyCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.response.*;
import system.stellar_stay.modules.properties.enums.PropertiesStatus;

import java.util.Set;
import java.util.UUID;

public interface PropertiesService {

    // Function for manager
    PropertiesResponseForManagerDTO requestCreatePropertiesForManager(PropertyCreateRequestForManagerDTO propertyCreateRequestForManagerDTO);
    PropertiesResponseForManagerDTO updatePropertiesForManager(UUID propertyId, PropertiesUpdateRequestForManagerDTO propertyUpdateRequestForManagerDTO);
    Page<ListPropertiesResponseForManager> getPropertiesByManagerId(int page, int size, String sortBy, String sortDir, String keyword);
    PropertyDetailResponseForManagerDTO getDetailPropertiesByIdForManager(UUID propertyId);



    // Function for admin
    void updatedStatusPropertiesForRequest(UUID propertyId, PropertiesStatus status, String reason);
    void updatePropertiesStatusForAdmin(UUID propertyId, PropertiesStatus status);
    Page<ListPropertiesResponseForAdmin> getAllPropertiesForAdmin(int page, int size, String sortBy, String sortDir, String keyword);
    PropertyDetailResponseForAdminDTO getDetailPropertiesByIdForAdmin(UUID propertyId);

}
