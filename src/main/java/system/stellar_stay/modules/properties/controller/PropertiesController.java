package system.stellar_stay.modules.properties.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import system.stellar_stay.modules.properties.dto.properties.request.PropertiesUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.request.PropertyCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.response.ListPropertiesResponseForAdminDTO;
import system.stellar_stay.modules.properties.dto.properties.response.ListPropertiesResponseForManagerDTO;
import system.stellar_stay.modules.properties.enums.PropertiesStatus;
import system.stellar_stay.modules.properties.service.PropertiesService;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.common.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PropertiesController {

    private final PropertiesService propertiesService;

    // For Manager

    // 1. Request to create property
    @Operation(summary = "API request to create properties", description = """
            API này cho phép người dùng gửi yêu cầu tạo một tài sản mới. 
            Người dùng sẽ cung cấp thông tin chi tiết về tài sản, bao gồm tên, mô tả, vị trí, giá cả và các tiện ích đi kèm. 
            Sau khi nhận được yêu cầu, hệ thống sẽ xử lý và lưu trữ thông tin tài sản vào cơ sở dữ liệu. 
            Nếu thành công, hệ thống sẽ trả về phản hồi xác nhận việc tạo tài sản mới đã hoàn tất.
            """)

    @PostMapping("/manager/properties")
    public ResponseEntity<ApiResponse<ListPropertiesResponseForManagerDTO>> requestCreateProperty(HttpServletRequest request, @RequestBody PropertyCreateRequestForManagerDTO propertyCreateRequestForManagerDTO) {

        return ResponseEntity.ok(
                ApiResponse.<ListPropertiesResponseForManagerDTO>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Request create properties successfully")
                        .result(propertiesService.requestCreatePropertiesForManager(propertyCreateRequestForManagerDTO))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // 2. Update properties for manager
    @Operation(summary = "API request to update properties", description = """
            API này để thằng manager tự update thông tin của properties cũng như cập nhật trạng thái của properties đó.
            Khi manager gửi yêu cầu cập nhật thông tin tài sản, hệ thống sẽ nhận và xử lý yêu cầu đó. 
            Manager có thể cập nhật các thông tin như tên, mô tả, vị trí, giá cả và các tiện ích đi kèm của tài sản. 
            Sau khi nhận được yêu cầu cập nhật, hệ thống sẽ kiểm tra tính hợp lệ của thông tin mới và nếu hợp lệ, hệ thống sẽ cập nhật thông tin tài sản trong cơ sở dữ liệu. 
            Nếu việc cập nhật thành công, hệ thống sẽ trả về phản hồi xác nhận việc cập nhật tài sản đã hoàn tất.
            """)

    @PutMapping("/manager/properties/{id}")
    public ResponseEntity<ApiResponse<ListPropertiesResponseForManagerDTO>> updatePropertyForManager(HttpServletRequest request,
                                                                                                     @RequestBody PropertiesUpdateRequestForManagerDTO propertiesUpdateRequestForManagerDTO,
                                                                                                     @PathVariable("id") UUID propertyId) {

        return ResponseEntity.ok(
                ApiResponse.<ListPropertiesResponseForManagerDTO>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Update properties for manager successfully")
                        .result(propertiesService.updatePropertiesForManager(propertyId, propertiesUpdateRequestForManagerDTO))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    // 3. Get All Properties by managerId
    @Operation(summary = "API get all properties by managerId", description = """
            API này cho phép người dùng lấy danh sách tất cả các tài sản mà họ đã tạo.
            Người dùng sẽ nhận được một danh sách các tài sản, mỗi tài sản sẽ bao gồm thông tin chi tiết như tên, mô tả, vị trí, giá cả và các tiện ích đi kèm. 
            API này giúp người dùng dễ dàng quản lý và theo dõi các tài sản mà họ đã tạo, cũng như có thể thực hiện các hành động khác như cập nhật hoặc xóa tài sản nếu cần thiết.
            """)

    @GetMapping("/manager/properties")
    public ResponseEntity<ApiResponse<Set<ListPropertiesResponseForManagerDTO>>> getAllPropertiesByAccountId(HttpServletRequest request){

        return ResponseEntity.ok(
                ApiResponse.<Set<ListPropertiesResponseForManagerDTO>>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Get all properties by managerId successfully")
                        .result(propertiesService.getPropertiesByManagerId())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

//    // 4. Get Property Detail for Manager
//    @Operation(summary = "API get property detail for manager", description = """
//            API này cho phép người dùng lấy thông tin chi tiết về một tài sản cụ thể mà họ đã tạo.
//            Người dùng sẽ cung cấp ID của tài sản mà họ muốn xem thông tin chi tiết.
//            Sau khi nhận được yêu cầu, hệ thống sẽ truy xuất thông tin chi tiết của tài sản từ cơ sở dữ liệu và trả về cho người dùng.
//            Thông tin chi tiết có thể bao gồm tên, mô tả, vị trí, giá cả và các tiện ích đi kèm của tài sản.
//            API này giúp người dùng dễ dàng quản lý và theo dõi các tài sản mà họ đã tạo, cũng như có thể thực hiện các hành động khác như cập nhật hoặc xóa tài sản nếu cần thiết.
//            """)

//    @GetMapping("/manager/properties/{id}")
//    public ResponseEntity<ApiResponse<PropertyResponseForManagerDTO>> getPropertyDetailForManager(HttpServletRequest request,
//                                                                                 @PathVariable("id") UUID propertyId){
//
//        return ResponseEntity.ok(
//                ApiResponse.<PropertyResponseForManagerDTO>builder()
//                        .code(ErrorCode.SUCCESS.getCode())
//                        .message("Get detail properties successfully")
//                        .result(propertiesService.getDetailPropertiesByIdForManager(propertyId))
//                        .path(request.getRequestURI())
//                        .timestamp(LocalDateTime.now())
//                        .build()
//        );
//    }
















    // For admin
    // 1. Update the request reate properties of manager
    @Operation(summary = "API update request create properties of manager", description = """
            API này để admin có thể cập nhật trạng thái của yêu cầu tạo tài sản mà manager đã gửi.
            Admin sẽ nhận được thông tin chi tiết về yêu cầu tạo tài sản, bao gồm tên, mô tả, vị trí, giá cả và các tiện ích đi kèm.
            Admin có thể xem xét và đánh giá yêu cầu dựa trên thông tin đã cung cấp, sau đó quyết định chấp nhận hoặc từ chối yêu cầu. 
            Nếu admin chấp nhận yêu cầu, hệ thống sẽ cập nhật trạng thái của tài sản thành "đã chấp nhận" và thông báo cho manager về quyết định này. 
            Nếu admin từ chối yêu cầu, hệ thống sẽ cập nhật trạng thái của tài sản thành "đã từ chối" và thông báo cho manager về quyết định này.
            """)

    @PutMapping("/admin/properties/{id}/status")
    public ResponseEntity<ApiResponse<Void>> requestCreateProperty(HttpServletRequest request,
                                                                   @PathVariable("id") UUID propertyId,
                                                                   @RequestParam("status")PropertiesStatus status,
                                                                   @RequestParam("reason") String reason) {

        propertiesService.updatedStatusPropertiesForRequest(propertyId, status, reason);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Decided request create properties successfully")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    // 2. Get all properties for admin
    @Operation(summary = "API get all properties for admin", description = """
            API này để admin có thể cập nhật trạng thái của yêu cầu tạo tài sản mà manager đã gửi.
            Admin sẽ nhận được thông tin chi tiết về yêu cầu tạo tài sản, bao gồm tên, mô tả, vị trí, giá cả và các tiện ích đi kèm.
            Admin có thể xem xét và đánh giá yêu cầu dựa trên thông tin đã cung cấp, sau đó quyết định chấp nhận hoặc từ chối yêu cầu. 
            Nếu admin chấp nhận yêu cầu, hệ thống sẽ cập nhật trạng thái của tài sản thành "đã chấp nhận" và thông báo cho manager về quyết định này. 
            Nếu admin từ chối yêu cầu, hệ thống sẽ cập nhật trạng thái của tài sản thành "đã từ chối" và thông báo cho manager về quyết định này.
            """)

    @GetMapping("/admin/properties")
    public ResponseEntity<ApiResponse<Page<ListPropertiesResponseForAdminDTO>>> getAllProperties(HttpServletRequest request,
                                                                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                                 @RequestParam(value = "sortBy", defaultValue = "createdAt") String sort,
                                                                                                 @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                                                                                                 @RequestParam(value = "keyword", required = false) String keyword) {


        return ResponseEntity.ok(
                ApiResponse.<Page<ListPropertiesResponseForAdminDTO>>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Decided request create properties successfully")
                        .result(propertiesService.getAllPropertiesForAdmin(page, size, sort, sortDir, keyword))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


}
