package system.stellar_stay.modules.properties.dto.properties.response;

import system.stellar_stay.modules.properties.enums.PropertiesStatus;
import system.stellar_stay.modules.properties.enums.PropertiesType;

import java.math.BigDecimal;
import java.util.UUID;

public record PropertiesResponseForManagerDTO(
        UUID propertiesId,
        String name,
        String slug,
        PropertiesType type,
        String description,
        String address,
        String city,
        String district,
        String ward,
        BigDecimal latitude,
        BigDecimal longitude,
        String phone,
        String email,
        PropertiesStatus status,
        Boolean isAvailable
) {
}
