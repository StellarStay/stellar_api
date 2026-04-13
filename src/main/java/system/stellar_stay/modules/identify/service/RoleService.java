package system.stellar_stay.modules.identify.service;

import system.stellar_stay.modules.identify.entity.Role;

public interface RoleService {
    Role findRoleByCode(String code);
}
