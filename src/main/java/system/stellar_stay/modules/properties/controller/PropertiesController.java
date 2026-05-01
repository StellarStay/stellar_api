package system.stellar_stay.modules.properties.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import system.stellar_stay.modules.properties.dto.properties.request.PropertiesUpdateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.request.PropertyCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.response.*;
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

    @PostMapping("/manager/property")
    public ResponseEntity<ApiResponse<PropertiesResponseForManagerDTO>> requestCreatePropertyForManager(HttpServletRequest request, @RequestBody PropertyCreateRequestForManagerDTO propertyCreateRequestForManagerDTO) {

        return ResponseEntity.ok(
                ApiResponse.<PropertiesResponseForManagerDTO>builder()
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

    @PutMapping("/manager/property/{id}")
    public ResponseEntity<ApiResponse<PropertiesResponseForManagerDTO>> updatePropertyForManager(HttpServletRequest request,
                                                                                                 @RequestBody PropertiesUpdateRequestForManagerDTO propertiesUpdateRequestForManagerDTO,
                                                                                                 @PathVariable("id") UUID propertyId) {

        return ResponseEntity.ok(
                ApiResponse.<PropertiesResponseForManagerDTO>builder()
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
    public ResponseEntity<ApiResponse<Page<ListPropertiesResponseForManager>>> getAllPropertiesByManagerId(HttpServletRequest request,
                                                                                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                                         @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                                         @RequestParam(value = "sortBy", defaultValue = "createdAt") String sort,
                                                                                                         @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                                                                                                         @RequestParam(value = "keyword", required = false) String keyword){

        return ResponseEntity.ok(
                ApiResponse.<Page<ListPropertiesResponseForManager>>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Get all properties by managerId successfully")
                        .result(propertiesService.getPropertiesByManagerId(page, size, sort, sortDir, keyword))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // 4. Get Property Detail for Manager
    @Operation(summary = "API get property detail for manager", description = """
            API này cho phép người dùng lấy thông tin chi tiết về một tài sản cụ thể mà họ đã tạo.
            Người dùng sẽ cung cấp ID của tài sản mà họ muốn xem thông tin chi tiết.
            Sau khi nhận được yêu cầu, hệ thống sẽ truy xuất thông tin chi tiết của tài sản từ cơ sở dữ liệu và trả về cho người dùng.
            Thông tin chi tiết có thể bao gồm tên, mô tả, vị trí, giá cả và các tiện ích đi kèm của tài sản.
            API này giúp người dùng dễ dàng quản lý và theo dõi các tài sản mà họ đã tạo, cũng như có thể thực hiện các hành động khác như cập nhật hoặc xóa tài sản nếu cần thiết.
            """)

    @GetMapping("/manager/property/{id}")
    public ResponseEntity<ApiResponse<PropertyDetailResponseForManagerDTO>> getPropertyDetailForManager(HttpServletRequest request,
                                                                                                        @PathVariable("id") UUID propertyId){

        return ResponseEntity.ok(
                ApiResponse.<PropertyDetailResponseForManagerDTO>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Get detail properties successfully")
                        .result(propertiesService.getDetailPropertiesByIdForManager(propertyId))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
















    // For admin
    // 1. Update the request create properties of manager
    @Operation(summary = "API update request create properties of manager", description = """
            API này để admin có thể cập nhật trạng thái của yêu cầu tạo tài sản mà manager đã gửi.
            Admin sẽ nhận được thông tin chi tiết về yêu cầu tạo tài sản, bao gồm tên, mô tả, vị trí, giá cả và các tiện ích đi kèm.
            Admin có thể xem xét và đánh giá yêu cầu dựa trên thông tin đã cung cấp, sau đó quyết định chấp nhận hoặc từ chối yêu cầu. 
            Nếu admin chấp nhận yêu cầu, hệ thống sẽ cập nhật trạng thái của tài sản thành "đã chấp nhận" và thông báo cho manager về quyết định này. 
            Nếu admin từ chối yêu cầu, hệ thống sẽ cập nhật trạng thái của tài sản thành "đã từ chối" và thông báo cho manager về quyết định này.
            """)

    @PutMapping("/admin/property/{id}/decision")
    public ResponseEntity<ApiResponse<Void>> updateRequestCreatePropertyForAdmin(HttpServletRequest request,
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

    // 2. Update status property of manager
    @Operation(summary = "API update status properties of manager", description = """
            API này để admin có thể cập nhật trạng thái của property khi mà admin tự thấy property đó có sự cố hay sao đó
            Không muốn để property đó tiếp tục hoạt động nữa thì admin sẽ cập nhật trạng thái của property đó thành "đã ngừng hoạt động" hoặc "đã xóa" tùy vào tình huống cụ thể. 
            Khi admin cập nhật trạng thái của property, hệ thống sẽ lưu trữ thông tin về việc cập nhật này, bao gồm lý do và thời gian cập nhật, để có thể theo dõi và quản lý các tài sản một cách hiệu quả.
            API này giúp admin dễ dàng quản lý và kiểm soát các tài sản, đảm bảo rằng chỉ những tài sản phù hợp và đáp ứng tiêu chuẩn mới được phép hoạt động trên nền tảng.
            """)

    @PutMapping("/admin/property/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateRequestCreatePropertyForAdmin(HttpServletRequest request,
                                                                                 @PathVariable("id") UUID propertyId,
                                                                                 @RequestParam("status")PropertiesStatus status) {

        propertiesService.updatePropertiesStatusForAdmin(propertyId, status);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Update status of property successfully")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    // 3. Get all properties for admin
    @Operation(summary = "API get all properties for admin", description = """
            API này để admin có thể cập nhật trạng thái của yêu cầu tạo tài sản mà manager đã gửi.
            Admin sẽ nhận được thông tin chi tiết về yêu cầu tạo tài sản, bao gồm tên, mô tả, vị trí, giá cả và các tiện ích đi kèm.
            Admin có thể xem xét và đánh giá yêu cầu dựa trên thông tin đã cung cấp, sau đó quyết định chấp nhận hoặc từ chối yêu cầu. 
            Nếu admin chấp nhận yêu cầu, hệ thống sẽ cập nhật trạng thái của tài sản thành "đã chấp nhận" và thông báo cho manager về quyết định này. 
            Nếu admin từ chối yêu cầu, hệ thống sẽ cập nhật trạng thái của tài sản thành "đã từ chối" và thông báo cho manager về quyết định này.
            """)

    @GetMapping("/admin/properties")
    public ResponseEntity<ApiResponse<Page<ListPropertiesResponseForAdmin>>> getAllPropertiesForAdmin(HttpServletRequest request,
                                                                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                              @RequestParam(value = "sortBy", defaultValue = "createdAt") String sort,
                                                                                              @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                                                                                              @RequestParam(value = "keyword", required = false) String keyword) {


        return ResponseEntity.ok(
                ApiResponse.<Page<ListPropertiesResponseForAdmin>>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Decided request create properties successfully")
                        .result(propertiesService.getAllPropertiesForAdmin(page, size, sort, sortDir, keyword))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // 4. Get property detail for admin
    @Operation(summary = "API get detail properties for admin", description = """
            API này cho phép admin lấy thông tin chi tiết về một tài sản cụ thể.
            Admin sẽ cung cấp ID của tài sản mà họ muốn xem thông tin chi tiết.
            Sau khi nhận được yêu cầu, hệ thống sẽ truy xuất thông tin chi tiết của tài sản từ cơ sở dữ liệu và trả về cho admin.
            Thông tin chi tiết có thể bao gồm tên, mô tả, vị trí, giá cả và các tiện ích đi kèm của tài sản, cũng như trạng thái hiện tại của tài sản đó.
            API này giúp admin dễ dàng quản lý và theo dõi các tài sản, cũng như có thể thực hiện các hành động khác như cập nhật trạng thái hoặc xóa tài sản nếu cần thiết.
            """)

    @GetMapping("/admin/property/{propertyId}")
    public ResponseEntity<ApiResponse<PropertyDetailResponseForAdminDTO>> getDetailPropertiesForAdmin(HttpServletRequest request,
                                                                                                        @PathVariable("propertyId") UUID propertyId) {


        return ResponseEntity.ok(
                ApiResponse.<PropertyDetailResponseForAdminDTO>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Decided request create properties successfully")
                        .result(propertiesService.getDetailPropertiesByIdForAdmin(propertyId))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


}
