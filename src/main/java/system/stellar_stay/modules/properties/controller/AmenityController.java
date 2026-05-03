package system.stellar_stay.modules.properties.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import system.stellar_stay.modules.properties.dto.amenities.request.AmenityForUpdateRequestDTO;
import system.stellar_stay.modules.properties.dto.amenities.request.AmenityRequestDTO;
import system.stellar_stay.modules.properties.dto.amenities.response.AmenityItemResponseDTO;
import system.stellar_stay.modules.properties.dto.properties.request.PropertyCreateRequestForManagerDTO;
import system.stellar_stay.modules.properties.dto.properties.response.PropertiesResponseForManagerDTO;
import system.stellar_stay.modules.properties.dto.room_amenities.request.AssignAmenitiesForRoomRequestDTO;
import system.stellar_stay.modules.properties.service.AmenityServices;
import system.stellar_stay.shared.common.exception.ErrorCode;
import system.stellar_stay.shared.common.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/amenity")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityServices amenityServices;


    // For ADMIN

    // 1. Create Amenities
    @Operation(summary = "API create amenities", description = """
        API này cho phép admin tạo mới một tiện ích (amenity) cho hệ thống. 
        Admin sẽ cung cấp thông tin chi tiết về tiện ích cần tạo, bao gồm tên, mô tả và loại tiện ích. 
        Sau khi nhận được yêu cầu, hệ thống sẽ xử lý và lưu trữ thông tin tiện ích mới vào cơ sở dữ liệu, 
        đồng thời trả về phản hồi xác nhận việc tạo thành công cùng với thông tin chi tiết của tiện ích vừa được tạo.
            """)

    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<AmenityItemResponseDTO>> createAmenities(HttpServletRequest request,
                                                                               @RequestBody AmenityRequestDTO amenityRequest) {

        return ResponseEntity.ok(
                ApiResponse.<AmenityItemResponseDTO>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Request create properties successfully")
                        .result(amenityServices.createAmenity(amenityRequest))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    // 2. Update Information Amenities
    @Operation(summary = "API update information amenities", description = """
        API này cho phép admin cập nhật thông tin của một tiện ích (amenity) đã tồn tại trong hệ thống. 
        Admin sẽ cung cấp ID của tiện ích cần cập nhật cùng với thông tin mới bao gồm tên, mô tả và loại tiện ích. 
        Hệ thống sẽ kiểm tra tính hợp lệ của ID và thông tin mới, sau đó tiến hành cập nhật dữ liệu trong cơ sở dữ liệu. 
        Sau khi cập nhật thành công, hệ thống sẽ trả về phản hồi xác nhận việc cập nhật cùng với thông tin chi tiết của tiện ích đã được cập nhật.
            """)

    @PutMapping("/admin/{amenityId}")
    public ResponseEntity<ApiResponse<AmenityItemResponseDTO>> updatedInformationAmenity(HttpServletRequest request,
                                                                                        @PathVariable("amenityId") UUID amenityId,
                                                                                        @RequestBody AmenityForUpdateRequestDTO amenityUpdateRequest) {

        return ResponseEntity.ok(
                ApiResponse.<AmenityItemResponseDTO>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Update amenity successfully")
                        .result(amenityServices.updateInformationOfAmenity(amenityId, amenityUpdateRequest))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // 3. Delete Information Amenities
    @Operation(summary = "API delete information amenities", description = """
        API này cho phép admin xóa một hoặc nhiều tiện ích (amenity) đã tồn tại trong hệ thống.
        Admin sẽ cung cấp danh sách ID của các tiện ích cần xóa. Hệ thống sẽ kiểm tra tính hợp lệ của các ID và tiến hành xóa dữ liệu tương ứng trong cơ sở dữ liệu. 
        Sau khi xóa thành công, hệ thống sẽ trả về phản hồi xác nhận việc xóa cùng với thông tin chi tiết về các tiện ích đã được xóa.
    """)

    @DeleteMapping("/admin")
    public ResponseEntity<ApiResponse<AmenityItemResponseDTO>> deleteAmenity(HttpServletRequest request,
                                                                                         @RequestBody List<UUID> amenitiesIds) {


        amenityServices.deleteAmenity(amenitiesIds);

        return ResponseEntity.ok(
                ApiResponse.<AmenityItemResponseDTO>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Request create properties successfully")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }



    // For Manager

    // 1. Get All Amenities by roomId
    @Operation(summary = "API get all amenities by roomId", description = """
        API này cho phép manager lấy danh sách tất cả các tiện ích (amenity) liên quan đến một phòng cụ thể trong hệ thống. 
        Manager sẽ cung cấp ID của phòng cần truy vấn cùng với các tham số tùy chọn như từ khóa tìm kiếm, phân trang và sắp xếp. 
        Hệ thống sẽ kiểm tra tính hợp lệ của ID phòng và các tham số, sau đó truy vấn cơ sở dữ liệu để lấy danh sách tiện ích tương ứng. 
        Sau khi truy vấn thành công, hệ thống sẽ trả về phản hồi chứa danh sách các tiện ích liên quan đến phòng đã được yêu cầu.
    """)

    @GetMapping("/manager/{roomId}")
    public ResponseEntity<ApiResponse<Page<AmenityItemResponseDTO>>> getAllAmenitiesByRoomId(HttpServletRequest request,
                                                                                             @PathVariable("roomId") UUID roomId,
                                                                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                             @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                             @RequestParam(value = "sortBy", defaultValue = "createdAt") String sort,
                                                                                             @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
                                                                                             @RequestParam(value = "keyword", required = false) String keyword) {


        return ResponseEntity.ok(
                ApiResponse.<Page<AmenityItemResponseDTO>>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Get all amenities by roomId successfully")
                        .result(amenityServices.getAllAmenitiesByRoomId(roomId, keyword, page, size, sortDir, sort))
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    // 2. Assign Amenities For Room
    @Operation(summary = "API assign amenities for room", description = """
        API này cho phép manager gán các tiện ích (amenity) cho một phòng cụ thể trong hệ thống.
        Manager sẽ cung cấp ID của phòng cần gán cùng với danh sách ID của các tiện ích cần gán. 
        Hệ thống sẽ kiểm tra tính hợp lệ của ID phòng và các ID tiện ích, sau đó tiến hành cập nhật cơ sở dữ liệu để gán các tiện ích tương ứng cho phòng đã được yêu cầu. 
        Sau khi gán thành công, hệ thống sẽ trả về phản hồi xác nhận việc gán cùng với thông tin chi tiết về các tiện ích đã được gán cho phòng.
    """)

    @PostMapping("/manager/assign/{roomId}")
    public ResponseEntity<ApiResponse<Void>> assignAmenitiesForRoom(HttpServletRequest request,
                                                                    @PathVariable("roomId") UUID roomId,
                                                                    @RequestBody AssignAmenitiesForRoomRequestDTO requestDTO) {

        amenityServices.assignAmenitiesForRooms(roomId, requestDTO);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(ErrorCode.SUCCESS.getCode())
                        .message("Assign amenities for room successfully")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
