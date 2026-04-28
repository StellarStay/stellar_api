package system.stellar_stay.modules.properties.dto.properties.response;

import java.util.UUID;

public record ManagerResponse(
        UUID managerId,
        String managerName,
        String email,
        String phone
) {
}
