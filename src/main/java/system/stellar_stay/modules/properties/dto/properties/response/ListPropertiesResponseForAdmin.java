package system.stellar_stay.modules.properties.dto.properties.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import system.stellar_stay.modules.properties.enums.PropertiesStatus;
import system.stellar_stay.modules.properties.enums.PropertiesType;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ListPropertiesResponseForAdmin {
    private UUID propertiesId;
    private String name;
    private String slug;
    private PropertiesType type;
    private String description;
    private String address;
    private String city;
    private String district;
    private String ward;
    private String phone;
    private String email;
    private ManagerResponse manager;
    private String urlThumbnailImage;
}
