package system.stellar_stay.modules.identify.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import system.stellar_stay.modules.identify.dto.login.request.LoginRequestDTO;
import system.stellar_stay.modules.identify.dto.permissions.PermissionGroupResponseDTO;
import system.stellar_stay.modules.identify.dto.roles.request.RoleRequestDTO;
import system.stellar_stay.modules.identify.dto.roles.response.RoleResponseDTO;
import system.stellar_stay.modules.identify.entity.Permission;
import system.stellar_stay.modules.identify.service.PermissionService;
import system.stellar_stay.modules.identify.service.RoleService;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.common.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final PermissionService permissionService;
    private final RoleService roleService;

    @Operation(summary = "API Create Role", description = """
            Admin dùng để tạo role + permisison cho role đó
            """)

    @PostMapping("")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> createRole(HttpServletRequest request, @RequestBody RoleRequestDTO roleRequestDTO) {

        return ResponseEntity.ok(ApiResponse.<RoleResponseDTO>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Insert role successful")
                .result(roleService.insertRole(roleRequestDTO))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("{roleId}")
    @Operation(summary = "API Update Role", description = """
            Admin dùng để cập nhật role + permisison cho role đó
            """)
    public ResponseEntity<ApiResponse<RoleResponseDTO>> updateRole(HttpServletRequest request, @PathVariable("roleId") UUID roleId, @RequestBody RoleRequestDTO roleRequestDTO) {
        return ResponseEntity.ok(ApiResponse.<RoleResponseDTO>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Update role successful")
                .result(roleService.updateRole(roleId, roleRequestDTO))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping("{roleId}")
    @Operation(summary = "API Delete Role", description = """
            Admin dùng để xóa role
            """)
    public ResponseEntity<ApiResponse<Void>> deleteRole(HttpServletRequest request, @PathVariable("roleId") UUID roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Delete role successful")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }



    @Operation(summary = "API get all permission", description = """
            Lấy ra hết tất cả các permission hiện có trong hệ thống
            """)

    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<Set<PermissionGroupResponseDTO>>> getAllPermissions(HttpServletRequest request) {

        return ResponseEntity.ok(ApiResponse.<Set<PermissionGroupResponseDTO>>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Get all permissions successful")
                .result(permissionService.getAllPermissions())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
    }

}
