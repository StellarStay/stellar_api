package system.stellar_stay.modules.identify.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import system.stellar_stay.modules.identify.entity.Role;
import system.stellar_stay.modules.identify.repository.RoleRepository;
import system.stellar_stay.modules.identify.service.RoleService;
import system.stellar_stay.shared.common.exception.ApiException;
import system.stellar_stay.shared.common.exception.ErrorCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role findRoleByCode(String code) {
        if (code == null || code.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Role code cannot be null or empty");
        }
        Role role = roleRepository.findByCode(code);
        if (role == null) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Role code not found");
        }
        return role;
    }
}
