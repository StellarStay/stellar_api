package system.stellar_stay.modules.properties.dto.properties.request;

import system.stellar_stay.modules.properties.enums.PropertiesType;

import java.math.BigDecimal;

public record PropertiesUpdateRequestForManagerDTO(
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
        Boolean isAvailable
) {
}
